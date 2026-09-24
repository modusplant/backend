#!/usr/bin/env python3
"""Build a Docker Compose depends_on tree and render it as a single static HTML file.

Reads only Compose files: the root docker-compose.yml, docker-compose.override.yml,
and every file reached through `include:`. Never runs `docker compose config`.
"""
import html
import os
import re
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path

EXIT_MISSING_TOOL = 2

SKILL_DIR = Path(__file__).resolve().parents[1]
PROJECT_ROOT = SKILL_DIR.parents[2]
TEMPLATE_PATH = SKILL_DIR / "template.html"
RESULT_DIR = SKILL_DIR / "result"
ROOT_FILES = ["docker-compose.yml", "docker-compose.override.yml"]

DEFAULT_CONDITION = "service_started"
EDGE_STYLES = {
    "service_healthy": 'style=solid, color="#2e7d32"',
    "service_completed_successfully": 'style=bold, color="#1565c0"',
    "service_started": 'style=dashed, color="#424242"',
}
VARIABLE_PATTERN = re.compile(r"\$\{([A-Za-z_][A-Za-z0-9_]*)(?:(:?-)([^}]*))?\}|\$([A-Za-z_][A-Za-z0-9_]*)")


def check_tools():
    missing = []
    try:
        import yaml  # noqa: F401
    except ImportError:
        missing.append("PyYAML (python3 module): `sudo apt-get install python3-yaml` or `pip install pyyaml`")
    if shutil.which("dot") is None:
        missing.append("Graphviz (`dot` binary): `sudo apt-get install graphviz` or `brew install graphviz`")
    if missing:
        print("MISSING_TOOL: the following tools are required but not installed:")
        for item in missing:
            print(f"  - {item}")
        print("Install them, then run the skill again.")
        sys.exit(EXIT_MISSING_TOOL)


def make_loader():
    import yaml

    class ComposeLoader(yaml.SafeLoader):
        pass

    def construct_any_tag(loader, _suffix, node):
        if isinstance(node, yaml.MappingNode):
            return loader.construct_mapping(node, deep=True)
        if isinstance(node, yaml.SequenceNode):
            return loader.construct_sequence(node, deep=True)
        return loader.construct_scalar(node)

    ComposeLoader.add_multi_constructor("!", construct_any_tag)
    return ComposeLoader


def interpolate(value, warnings, context):
    """Resolve ${VAR} placeholders from the process environment only (never from .env files)."""
    unresolved = []

    def replace(match):
        name = match.group(1) or match.group(4)
        operator, default = match.group(2), match.group(3)
        current = os.environ.get(name)
        if operator == ":-" and not current:
            return default
        if operator == "-" and current is None:
            return default
        if current is None:
            unresolved.append(name)
            return match.group(0)
        return current

    result = VARIABLE_PATTERN.sub(replace, value)
    if unresolved:
        warnings.append(f"{context}: unresolved variable(s) {', '.join(unresolved)} in '{value}'; skipped")
        return None
    return result


def normalize_depends_on(raw):
    if raw is None:
        return {}
    if isinstance(raw, list):
        return {str(target): {"condition": DEFAULT_CONDITION, "required": True} for target in raw}
    if isinstance(raw, dict):
        normalized = {}
        for target, options in raw.items():
            options = options or {}
            normalized[str(target)] = {
                "condition": options.get("condition", DEFAULT_CONDITION),
                "required": options.get("required", True) not in (False, "false"),
            }
        return normalized
    return {}


class ComposeGraph:
    def __init__(self):
        self.loader = make_loader()
        self.services = {}
        self.groups = []
        self.warnings = []
        self.visited = set()

    def load_yaml(self, path):
        import yaml

        try:
            with open(path, encoding="utf-8") as file:
                return yaml.load(file, Loader=self.loader) or {}
        except FileNotFoundError:
            self.warnings.append(f"file not found: {path}")
        except PermissionError:
            self.warnings.append(f"permission denied: {path}")
        except yaml.YAMLError as error:
            self.warnings.append(f"YAML parse error in {path}: {error}")
        return None

    def load_group(self, paths, optional_paths=()):
        """Load one Compose model: the first path is primary, later paths override it."""
        primary = paths[0]
        if primary in self.visited:
            self.warnings.append(f"include cycle or duplicate include skipped: {primary}")
            return
        self.visited.add(primary)
        group_index = len(self.groups)
        self.groups.append(primary)

        for path in list(paths) + [p for p in optional_paths if p.exists()]:
            document = self.load_yaml(path)
            if document is None:
                continue
            self.load_includes(document.get("include") or [], path.parent)
            for name, definition in (document.get("services") or {}).items():
                self.merge_service(str(name), definition or {}, group_index, path)

    def load_includes(self, entries, base_dir):
        for entry in entries:
            raw_paths = entry if isinstance(entry, str) else (entry or {}).get("path")
            if isinstance(raw_paths, str):
                raw_paths = [raw_paths]
            if not raw_paths:
                self.warnings.append(f"include entry without path ignored: {entry}")
                continue
            resolved = []
            for raw_path in raw_paths:
                value = interpolate(str(raw_path), self.warnings, f"include in {base_dir}")
                if value is None:
                    break
                path = Path(value).expanduser()
                resolved.append((path if path.is_absolute() else base_dir / path).resolve())
            else:
                self.load_group(resolved)

    def merge_service(self, name, definition, group_index, source_path):
        if "extends" in definition:
            self.warnings.append(f"service '{name}' in {source_path} uses extends; extended depends_on is not followed")
        service = self.services.get(name)
        if service is None:
            service = {"group": group_index, "container_name": None, "depends_on": {}}
            self.services[name] = service
        elif service["group"] != group_index:
            self.warnings.append(f"service '{name}' is defined in multiple Compose models; merged")
        if definition.get("container_name"):
            service["container_name"] = str(definition["container_name"])
        service["depends_on"].update(normalize_depends_on(definition.get("depends_on")))

    def edges(self):
        for source, service in self.services.items():
            for target, options in service["depends_on"].items():
                yield source, target, options


def quote(value):
    return '"' + str(value).replace("\\", "\\\\").replace('"', '\\"') + '"'


def to_dot(graph):
    lines = [
        "digraph depends_on {",
        '  graph [rankdir=TB, fontname="Helvetica", fontsize=11, nodesep=0.5, ranksep=0.7, bgcolor="white"];',
        '  node [shape=box, style="rounded,filled", fillcolor="#f5f7fa", color="#90a4ae", fontname="Helvetica", fontsize=11];',
        '  edge [fontname="Helvetica", fontsize=9, arrowsize=0.7];',
    ]
    for index, primary in enumerate(graph.groups):
        members = [name for name, service in graph.services.items() if service["group"] == index]
        if not members:
            continue
        lines.append(f"  subgraph cluster_{index} {{")
        lines.append(f'    label={quote(primary)}; style="rounded,dashed"; color="#b0bec5"; fontcolor="#546e7a";')
        for name in sorted(members):
            container = graph.services[name]["container_name"]
            label = f"{name}\n({container})" if container and container != name else name
            lines.append(f"    {quote(name)} [label={quote(label)}];")
        lines.append("  }")

    undefined = sorted({target for _, target, _ in graph.edges() if target not in graph.services})
    for name in undefined:
        lines.append(f'  {quote(name)} [label={quote(name + chr(10) + "(undefined)")}, style="rounded,dashed", color="#c62828", fontcolor="#c62828", fillcolor="white"];')

    for source, target, options in sorted(graph.edges()):
        style = EDGE_STYLES.get(options["condition"], 'style=dotted, color="#6a1b9a"')
        if not options["required"]:
            style = style.split(", color=")[0] + ', color="#9e9e9e", fontcolor="#9e9e9e"'
        label = options["condition"].removeprefix("service_") + ("" if options["required"] else " (optional)")
        # Declared dependency-first with dir=back so root containers rank at the top
        # while the arrowhead still points from dependent to dependency.
        lines.append(f"  {quote(target)} -> {quote(source)} [{style}, dir=back, label={quote(label)}];")
    lines.append("}")
    return "\n".join(lines), undefined


def render_svg(dot_source):
    completed = subprocess.run(["dot", "-Tsvg"], input=dot_source, capture_output=True, text=True, check=False)
    if completed.returncode != 0:
        print(f"ERROR: Graphviz failed: {completed.stderr.strip()}")
        sys.exit(1)
    svg = completed.stdout
    return svg[svg.find("<svg"):]


def main():
    check_tools()
    graph = ComposeGraph()
    root_file, override_file = (PROJECT_ROOT / name for name in ROOT_FILES)
    if not root_file.exists():
        print(f"ERROR: {root_file} not found")
        sys.exit(1)
    graph.load_group([root_file], optional_paths=[override_file])

    dot_source, undefined = to_dot(graph)
    svg = render_svg(dot_source)
    edge_count = sum(1 for _ in graph.edges())
    now = datetime.now()

    warnings_html = "".join(f"<li>{html.escape(warning)}</li>" for warning in graph.warnings) or "<li>None</li>"
    summary = f"{len(graph.services)} services, {edge_count} depends_on edges, {len(graph.groups)} Compose file groups"
    page = (
        TEMPLATE_PATH.read_text(encoding="utf-8")
        .replace("{{GENERATED_AT}}", now.strftime("%Y-%m-%d %H:%M"))
        .replace("{{SUMMARY}}", html.escape(summary))
        .replace("{{WARNINGS}}", warnings_html)
        .replace("{{SVG}}", svg)
    )
    RESULT_DIR.mkdir(exist_ok=True)
    output_path = RESULT_DIR / f"docker_dependency_{now.strftime('%Y%m%d%H%M')}.html"
    output_path.write_text(page, encoding="utf-8")

    print(f"OUTPUT: {output_path.relative_to(PROJECT_ROOT)}")
    print(f"SUMMARY: {summary}")
    if undefined:
        print(f"UNDEFINED_TARGETS: {', '.join(undefined)}")
    for warning in graph.warnings:
        print(f"WARNING: {warning}")


if __name__ == "__main__":
    main()
