#!/usr/bin/env python3
"""Look up, draft, and register Korean labels in this skill's glossary/*.tsv files.

Commands:
  lookup table <table>...
  lookup column <table> <column>...
  draft table <table>...
  draft column <table> <column>...
  add table <table> <label>
  add column <column> <table|-> <label>

Output lines are tab-separated and start with FOUND, MISSING, DRAFT, ADDED, EXISTS, or ERROR.
"""
import re
import sys
from pathlib import Path

EXIT_ERROR = 1

SKILL_DIR = Path(__file__).resolve().parents[1]
GLOSSARY_DIR = SKILL_DIR / "glossary"

HEADERS = {
    "table": ["table", "label"],
    "column": ["column", "table", "label"],
    "token": ["token", "scope", "korean"],
    "pattern": ["pattern", "korean"],
}
IDENTIFIER = re.compile(r"^[a-z0-9_]+$")
GENERIC_TABLE = "-"


class GlossaryError(Exception):
    pass


def load(name):
    path = GLOSSARY_DIR / f"{name}.tsv"
    if not path.is_file():
        raise GlossaryError(f"missing file {path}")
    lines = path.read_text(encoding="utf-8").splitlines()
    if not lines or lines[0].split("\t") != HEADERS[name]:
        raise GlossaryError(f"{path.name}: header must be {'<TAB>'.join(HEADERS[name])}")
    rows = []
    for number, line in enumerate(lines[1:], start=2):
        fields = line.split("\t")
        if len(fields) != len(HEADERS[name]):
            raise GlossaryError(f"{path.name}:{number}: expected {len(HEADERS[name])} fields")
        rows.append(fields)
    return rows


def append(name, fields):
    path = GLOSSARY_DIR / f"{name}.tsv"
    content = path.read_text(encoding="utf-8")
    if content and not content.endswith("\n"):
        content += "\n"
    path.write_text(content + "\t".join(fields) + "\n", encoding="utf-8")


def require_identifier(value):
    if not IDENTIFIER.match(value):
        raise GlossaryError(f"invalid identifier '{value}'")


def require_label(value):
    if not value.strip() or "\t" in value or "\n" in value:
        raise GlossaryError("label must be non-empty and contain no tab or newline")


def table_label(rows, table):
    return next((label for key, label in rows if key == table), None)


def column_label(rows, table, column):
    specific = next((label for key, scope, label in rows if key == column and scope == table), None)
    if specific is not None:
        return specific
    return next((label for key, scope, label in rows if key == column and scope == ""), None)


def translate(name, scope):
    """Greedy longest-match of underscore-separated fragments against the token table."""
    tokens = {}
    for token, token_scope, korean in load("token"):
        if token_scope in ("", scope):
            tokens.setdefault(token, korean)
    fragments = name.split("_")
    words, unresolved = [], []
    index = 0
    while index < len(fragments):
        for end in range(len(fragments), index, -1):
            candidate = "_".join(fragments[index:end])
            if candidate in tokens:
                words.append(tokens[candidate])
                index = end
                break
        else:
            words.append(f"?{fragments[index]}")
            unresolved.append(fragments[index])
            index += 1
    return " ".join(words), unresolved


def match_pattern(column):
    for pattern, korean in load("pattern"):
        regex = "^" + re.escape(pattern).replace(re.escape("<x>"), "(.+)") + "$"
        found = re.match(regex, column)
        if found:
            return pattern, korean, found.group(1)
    return None, None, column


def lookup_table(tables):
    rows = load("table")
    for table in tables:
        label = table_label(rows, table)
        print(f"FOUND\t{table}\t{label}" if label is not None else f"MISSING\t{table}")


def lookup_column(table, columns):
    rows = load("column")
    for column in columns:
        label = column_label(rows, table, column)
        print(f"FOUND\t{table}\t{column}\t{label}" if label is not None else f"MISSING\t{table}\t{column}")


def draft_table(tables):
    rows = load("table")
    for table in tables:
        label = table_label(rows, table)
        if label is not None:
            print(f"FOUND\t{table}\t{label}")
            continue
        candidate, unresolved = translate(table, "table")
        print(f"DRAFT\t{table}\t{candidate}\tunresolved={','.join(unresolved)}")


def draft_column(table, columns):
    rows = load("column")
    for column in columns:
        label = column_label(rows, table, column)
        if label is not None:
            print(f"FOUND\t{table}\t{column}\t{label}")
            continue
        pattern, korean, stem = match_pattern(column)
        stem_label, unresolved = translate(stem, "column")
        candidate = korean.replace("<x>", stem_label) if pattern else stem_label
        print(f"DRAFT\t{table}\t{column}\t{candidate}\tpattern={pattern or ''}\tunresolved={','.join(unresolved)}")


def add_table(table, label):
    require_identifier(table)
    require_label(label)
    current = table_label(load("table"), table)
    if current is not None:
        print(f"EXISTS\t{table}\t{current}")
        return
    append("table", [table, label])
    print(f"ADDED\t{table}\t{label}")


def add_column(column, table, label):
    require_identifier(column)
    scope = "" if table == GENERIC_TABLE else table
    if scope:
        require_identifier(scope)
    require_label(label)
    rows = load("column")
    current = next((row_label for key, row_scope, row_label in rows if key == column and row_scope == scope), None)
    if current is not None:
        print(f"EXISTS\t{table}\t{column}\t{current}")
        return
    append("column", [column, scope, label])
    print(f"ADDED\t{table}\t{column}\t{label}")


def run(arguments):
    if len(arguments) < 3:
        raise GlossaryError("usage: see the module docstring")
    command, kind, rest = arguments[0], arguments[1], arguments[2:]
    if command == "lookup" and kind == "table":
        lookup_table(rest)
    elif command == "lookup" and kind == "column" and len(rest) >= 2:
        lookup_column(rest[0], rest[1:])
    elif command == "draft" and kind == "table":
        draft_table(rest)
    elif command == "draft" and kind == "column" and len(rest) >= 2:
        draft_column(rest[0], rest[1:])
    elif command == "add" and kind == "table" and len(rest) == 2:
        add_table(*rest)
    elif command == "add" and kind == "column" and len(rest) == 3:
        add_column(*rest)
    else:
        raise GlossaryError("usage: see the module docstring")


def main():
    try:
        run(sys.argv[1:])
    except GlossaryError as error:
        print(f"ERROR\t{error}")
        sys.exit(EXIT_ERROR)


if __name__ == "__main__":
    main()
