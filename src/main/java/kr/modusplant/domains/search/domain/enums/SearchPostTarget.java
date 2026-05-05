package kr.modusplant.domains.search.domain.enums;

import lombok.Getter;

@Getter
public enum SearchPostTarget {
    TITLE("title"),
    CONTENT("content"),
    TITLE_CONTENT("title_content"),
    TITLE_CONTENT_COMMENT("title_content_comment");

    private final String value;

    SearchPostTarget(String value) {
        this.value = value;
    }
}
