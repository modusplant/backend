#!/usr/bin/env python3
"""커뮤니티 웹페이지 URL을 가져오기 -> 필터 체인 -> 최종 반환 순서로 처리한다.

`fetch`: Wikidata 전역 쿼리, 한국어 위키백과 분류, 웹 검색 URL을 소스별 최대 개수만큼 가져와 candidate-url.json에 저장한다.
`filter-reachability`: 후보 URL의 도달 가능 여부를 판정해 도달 불가 URL에는 탈락 사유를, 봇 요청을 차단한 URL에는 차단 사유를 기록한다.
`write-result`: 적합성 탈락 URL을 기록하고, 남은 후보 중 차단되지 않은 URL과 사용자 지정 URL은 result-url.txt로, 차단된 URL은 blocked-url.txt로 저장한다.
"""
import argparse
import json
import os
import re
import sys
import tempfile
import urllib.error
import urllib.parse
import urllib.request
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

SKILL_DIR = Path(__file__).resolve().parents[1]
PROJECT_ROOT = SKILL_DIR.parents[2]
SPARQL_PATH = SKILL_DIR / "query" / "select-url-from-global-wiki.sparql"
URL_DIR = SKILL_DIR / "url"
CUSTOM_PATH = URL_DIR / "custom-url.txt"
KOREAN_WIKI_UNRESOLVED_PATH = SKILL_DIR / "scripts" / "unresolved-korean-wiki-url.txt"
CANDIDATE_PATH = URL_DIR / "candidate-url.json"
RESULT_PATH = URL_DIR / "result-url.txt"
BLOCKED_PATH = URL_DIR / "blocked-url.txt"

USER_AGENT = "fetch-community-webpage-url/1.0 (Claude Code skill)"
# 봇 차단으로 인한 오판을 줄이기 위해 도달 가능 여부 확인에는 브라우저 UA를 사용한다
CHECK_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36"
TIMEOUT_SECONDS = 60
CHECK_TIMEOUT_SECONDS = 20
CHECK_WORKERS = 16
# 사이트는 살아 있으나 봇 요청을 거부할 때 반환되는 상태 코드
BLOCKED_STATUS_CODES = (400, 401, 403, 429)
BATCH_SIZE = 50
DEFAULT_FETCH_COUNT = 50

WIKIDATA_SPARQL_ENDPOINT = "https://query.wikidata.org/sparql"
WIKIDATA_API_ENDPOINT = "https://www.wikidata.org/w/api.php"
KOWIKI_API_ENDPOINT = "https://ko.wikipedia.org/w/api.php"
KOREAN_WIKI_PAGE_PREFIX = "https://ko.wikipedia.org/wiki/"
KOREAN_WIKI_CATEGORY = "분류:대한민국의_인터넷_커뮤니티"

INFOBOX_FIELD_PATTERN = re.compile(r"^\s*\|\s*(?:url|웹사이트|홈페이지|website)\s*=\s*(.*)$", re.IGNORECASE | re.MULTILINE)
URL_TEMPLATE_PATTERN = re.compile(r"\{\{\s*url\s*\|\s*([^|}]+)", re.IGNORECASE)
URL_PATTERN = re.compile(r"https?://[^\s\]|}<>\"']+", re.IGNORECASE)
BARE_HOST_PATTERN = re.compile(r"^(?:www\.)?[a-z0-9-]+(?:\.[a-z0-9-]+)+/?$", re.IGNORECASE)


class FetchError(Exception):
    pass


def request_json(endpoint, params, accept="application/json"):
    url = f"{endpoint}?{urllib.parse.urlencode(params)}"
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT, "Accept": accept})
    try:
        with urllib.request.urlopen(request, timeout=TIMEOUT_SECONDS) as response:
            return json.load(response)
    except (OSError, ValueError) as error:
        raise FetchError(f"{endpoint} request failed: {error}") from error


def normalize(raw_url):
    candidate = raw_url.strip()
    if not candidate:
        return None
    if "://" not in candidate:
        candidate = f"https://{candidate}"
    try:
        host = urllib.parse.urlsplit(candidate).hostname
    except ValueError:
        return None
    if not host or "." not in host:
        return None
    return f"https://{host.lower().rstrip('.')}/"


def read_url_file(path):
    if not path.exists():
        return []
    return [line.strip() for line in path.read_text(encoding="utf-8").splitlines() if line.strip()]


def fetch_global_wiki(count):
    response = request_json(
        WIKIDATA_SPARQL_ENDPOINT,
        {"query": f"{SPARQL_PATH.read_text(encoding='utf-8').rstrip()}\nLIMIT {count}"},
        accept="application/sparql-results+json",
    )
    return [binding["website"]["value"] for binding in response["results"]["bindings"]]


def chunks(items, size):
    return [items[index:index + size] for index in range(0, len(items), size)]


def list_category_titles():
    # 하위 분류(예: 분류:디시인사이드)는 같은 이름의 본문 문서로 대체한다
    titles = []
    params = {
        "action": "query",
        "list": "categorymembers",
        "cmtitle": KOREAN_WIKI_CATEGORY,
        "cmnamespace": "0|14",
        "cmlimit": "500",
        "format": "json",
    }
    while True:
        response = request_json(KOWIKI_API_ENDPOINT, params)
        for member in response["query"]["categorymembers"]:
            title = member["title"]
            titles.append(title.split(":", 1)[1] if member["ns"] == 14 else title)
        if "continue" not in response:
            return list(dict.fromkeys(titles))
        params.update(response["continue"])


def extract_infobox_url(wikitext):
    for field_value in INFOBOX_FIELD_PATTERN.findall(wikitext):
        template_match = URL_TEMPLATE_PATTERN.search(field_value)
        if template_match:
            return template_match.group(1).strip()
        url_match = URL_PATTERN.search(field_value)
        if url_match:
            return url_match.group(0)
        bare_value = field_value.strip()
        if BARE_HOST_PATTERN.match(bare_value):
            return bare_value
    return None


def to_korean_wiki_page_url(title):
    return f"{KOREAN_WIKI_PAGE_PREFIX}{title.replace(' ', '_')}"


def read_unresolved_titles():
    titles = set()
    for line in read_url_file(KOREAN_WIKI_UNRESOLVED_PATH):
        if line.startswith(KOREAN_WIKI_PAGE_PREFIX):
            titles.add(urllib.parse.unquote(line[len(KOREAN_WIKI_PAGE_PREFIX):]).replace("_", " "))
    return titles


def fetch_korean_wiki(warnings, unresolved_titles, count):
    # 분류에서 얻은 원래 제목을 키로 삼아, 넘겨주기를 거쳐도 미해결 목록과 대조할 수 있게 한다
    pages = {}
    titles = [title for title in list_category_titles() if title not in unresolved_titles][:count]
    for title_batch in chunks(titles, BATCH_SIZE):
        response = request_json(KOWIKI_API_ENDPOINT, {
            "action": "query",
            "titles": "|".join(title_batch),
            "prop": "pageprops|revisions",
            "ppprop": "wikibase_item",
            "rvprop": "content",
            "rvslots": "main",
            "redirects": "1",
            "format": "json",
        })
        query = response["query"]
        resolved_titles = {}
        for title in title_batch:
            resolved = title
            for mapping in query.get("normalized", []) + query.get("redirects", []):
                if mapping["from"] == resolved:
                    resolved = mapping["to"]
            resolved_titles.setdefault(resolved, title)
        for page in query["pages"].values():
            original_title = resolved_titles.get(page["title"], page["title"])
            if "missing" in page:
                warnings.append(f"korean-wiki: page not found: {original_title}")
                continue
            revisions = page.get("revisions") or [{}]
            pages[original_title] = {
                "item": page.get("pageprops", {}).get("wikibase_item"),
                "wikitext": revisions[0].get("slots", {}).get("main", {}).get("*", ""),
            }

    official_websites = {}
    item_ids = [page["item"] for page in pages.values() if page["item"]]
    for id_batch in chunks(item_ids, BATCH_SIZE):
        response = request_json(WIKIDATA_API_ENDPOINT, {
            "action": "wbgetentities",
            "ids": "|".join(id_batch),
            "props": "claims",
            "format": "json",
        })
        for item_id, entity in response["entities"].items():
            claims = entity.get("claims", {}).get("P856", [])
            values = [claim["mainsnak"].get("datavalue", {}).get("value") for claim in claims if claim.get("rank") != "deprecated"]
            values = [value for value in values if value]
            if values:
                official_websites[item_id] = values[0]

    urls = []
    new_unresolved_titles = []
    for title, page in sorted(pages.items()):
        url = official_websites.get(page["item"]) or extract_infobox_url(page["wikitext"])
        if url:
            urls.append(url)
        else:
            new_unresolved_titles.append(title)
    return urls, new_unresolved_titles


def write_atomically(path, lines):
    descriptor, temporary_path = tempfile.mkstemp(dir=path.parent, prefix=f".{path.name}.")
    try:
        with os.fdopen(descriptor, "w", encoding="utf-8") as temporary_file:
            temporary_file.write("".join(f"{line}\n" for line in lines))
        os.replace(temporary_path, path)
    except BaseException:
        os.unlink(temporary_path)
        raise


def strip_www(host):
    return host[4:] if host.startswith("www.") else host


def check_reachability(url):
    # 도달 가능하면 None, 봇 요청이 차단되면 ("blocked", 사유), 도달 불가하면 ("reachability", 사유)를 반환한다
    request = urllib.request.Request(url, headers={"User-Agent": CHECK_USER_AGENT})
    try:
        with urllib.request.urlopen(request, timeout=CHECK_TIMEOUT_SECONDS) as response:
            final_host = urllib.parse.urlsplit(response.geturl()).hostname or ""
    except urllib.error.HTTPError as error:
        return ("blocked" if error.code in BLOCKED_STATUS_CODES else "reachability", f"status-{error.code}")
    except (OSError, ValueError) as error:
        return ("reachability", type(error).__name__)
    if strip_www(final_host.lower()) != strip_www(urllib.parse.urlsplit(url).hostname):
        return ("reachability", f"redirected-to-{final_host}")
    return None


def read_candidates():
    if not CANDIDATE_PATH.exists():
        raise FetchError(f"{CANDIDATE_PATH.relative_to(PROJECT_ROOT)} not found, run fetch first")
    try:
        return json.loads(CANDIDATE_PATH.read_text(encoding="utf-8"))
    except ValueError as error:
        raise FetchError(f"{CANDIDATE_PATH.relative_to(PROJECT_ROOT)} is malformed: {error}") from error


def write_candidates(candidates):
    write_atomically(CANDIDATE_PATH, [json.dumps(candidates, ensure_ascii=False, indent=2)])


def read_custom_urls(warnings):
    urls = set()
    for raw_url in read_url_file(CUSTOM_PATH):
        url = normalize(raw_url)
        if url:
            urls.add(url)
        else:
            warnings.append(f"custom: invalid URL skipped: {raw_url}")
    return urls


def print_report(summaries, warnings):
    for summary in summaries:
        print(f"SUMMARY: {summary}")
    for warning in warnings:
        print(f"WARNING: {warning}")


def run_fetch(count, web_search_urls):
    warnings = []
    unresolved_titles = read_unresolved_titles()
    korean_wiki_urls, new_unresolved_titles = fetch_korean_wiki(warnings, unresolved_titles, count)
    sources = {
        "global-wiki": fetch_global_wiki(count),
        "korean-wiki": korean_wiki_urls,
        "web-search": web_search_urls,
    }

    # 정규화 후 같은 URL은 하나의 후보로 합치고, 해당 URL을 가져온 소스를 모두 기록한다
    candidates = {}
    summaries = []
    for name, raw_urls in sources.items():
        normalized = set()
        for raw_url in raw_urls:
            url = normalize(raw_url)
            if url:
                normalized.add(url)
            else:
                warnings.append(f"{name}: invalid URL skipped: {raw_url}")
        for url in normalized:
            candidates.setdefault(url, {"url": url, "sources": [], "blockedBy": None, "droppedBy": None})["sources"].append(name)
        summaries.append(f"fetch {name}={len(normalized)}")

    write_candidates([candidates[url] for url in sorted(candidates)])
    if new_unresolved_titles:
        write_atomically(
            KOREAN_WIKI_UNRESOLVED_PATH,
            read_url_file(KOREAN_WIKI_UNRESOLVED_PATH) + [to_korean_wiki_page_url(title) for title in new_unresolved_titles],
        )

    print(f"OUTPUT: {CANDIDATE_PATH.relative_to(PROJECT_ROOT)}")
    summaries.append(f"fetch korean-wiki-unresolved={len(unresolved_titles) + len(new_unresolved_titles)} new={len(new_unresolved_titles)} "
                     f"file={KOREAN_WIKI_UNRESOLVED_PATH.relative_to(PROJECT_ROOT)}")
    summaries.append(f"fetch candidates={len(candidates)}")
    print_report(summaries, warnings)


def run_filter_reachability():
    warnings = []
    candidates = read_candidates()
    # 사용자 지정 URL은 필터 체인을 거치지 않는다
    custom_urls = read_custom_urls(warnings)
    targets = [candidate for candidate in candidates if candidate["droppedBy"] is None and candidate["url"] not in custom_urls]
    with ThreadPoolExecutor(max_workers=CHECK_WORKERS) as executor:
        outcomes = list(executor.map(check_reachability, [candidate["url"] for candidate in targets]))

    # 차단된 사이트는 탈락시키지 않고 적합성 판단 대상에 남긴다
    counts = {"reachability": 0, "blocked": 0}
    for candidate, outcome in zip(targets, outcomes):
        if not outcome:
            continue
        kind, reason = outcome
        counts[kind] += 1
        if kind == "blocked":
            candidate["blockedBy"] = reason
            print(f"BLOCKED: {candidate['url']} {reason}")
        else:
            candidate["droppedBy"] = f"reachability:{reason}"
            print(f"DROPPED: {candidate['url']} {candidate['droppedBy']}")
    write_candidates(candidates)

    print(f"OUTPUT: {CANDIDATE_PATH.relative_to(PROJECT_ROOT)}")
    print_report([f"filter reachability checked={len(targets)} dropped={counts['reachability']} blocked={counts['blocked']}"], warnings)


def run_write_result(rejected_raw_urls):
    warnings = []
    candidates = read_candidates()
    custom_urls = read_custom_urls(warnings)
    by_url = {candidate["url"]: candidate for candidate in candidates}

    for raw_url in rejected_raw_urls:
        url = normalize(raw_url)
        candidate = by_url.get(url)
        if not candidate:
            warnings.append(f"suitability: rejected URL is not a candidate: {raw_url}")
        elif candidate["droppedBy"] is not None:
            warnings.append(f"suitability: rejected URL is already dropped: {url} {candidate['droppedBy']}")
        elif url in custom_urls:
            warnings.append(f"suitability: rejected URL is a custom URL and kept: {url}")
        else:
            candidate["droppedBy"] = "suitability"
    write_candidates(candidates)

    # 적합성 판단까지 통과한 차단 사이트 URL은 결과에서 제외하고 별도 파일로만 관리한다
    survivors = {candidate["url"] for candidate in candidates if candidate["droppedBy"] is None and candidate["blockedBy"] is None}
    blocked = {candidate["url"] for candidate in candidates if candidate["droppedBy"] is None and candidate["blockedBy"] is not None}
    result = survivors | custom_urls
    # 차단 사이트 URL의 변경 규모를 보고하기 위해 덮어쓰기 전의 목록과 비교한다
    previous_blocked = set(read_url_file(BLOCKED_PATH))
    write_atomically(RESULT_PATH, sorted(result))
    write_atomically(BLOCKED_PATH, sorted(blocked))

    dropped_counts = {}
    for candidate in candidates:
        if candidate["droppedBy"] is not None:
            filter_name = candidate["droppedBy"].split(":", 1)[0]
            dropped_counts[filter_name] = dropped_counts.get(filter_name, 0) + 1
    print(f"OUTPUT: {RESULT_PATH.relative_to(PROJECT_ROOT)}")
    print(f"OUTPUT: {BLOCKED_PATH.relative_to(PROJECT_ROOT)}")
    print_report(
        [f"filter {name} dropped={dropped}" for name, dropped in sorted(dropped_counts.items())]
        + [f"result total={len(result)} candidates={len(survivors)} custom-only={len(custom_urls - survivors)}",
           f"blocked total={len(blocked)} added={len(blocked - previous_blocked)} removed={len(previous_blocked - blocked)}"],
        warnings,
    )


def positive_integer(value):
    number = int(value)
    if number < 1:
        raise argparse.ArgumentTypeError(f"must be a positive integer: {value}")
    return number


def main():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(dest="command", required=True)
    fetch_parser = subparsers.add_parser("fetch")
    fetch_parser.add_argument("--count", type=positive_integer, default=DEFAULT_FETCH_COUNT)
    fetch_parser.add_argument("--web-search", nargs="*", default=[])
    subparsers.add_parser("filter-reachability")
    result_parser = subparsers.add_parser("write-result")
    result_parser.add_argument("--reject", nargs="*", default=[])
    arguments = parser.parse_args()

    try:
        if arguments.command == "fetch":
            run_fetch(arguments.count, arguments.web_search)
        elif arguments.command == "filter-reachability":
            run_filter_reachability()
        else:
            run_write_result(arguments.reject)
    except FetchError as error:
        print(f"ERROR: {error}")
        sys.exit(1)


if __name__ == "__main__":
    main()
