package kr.modusplant.domains.post.usecase.enums;

import lombok.Getter;

@Getter
public enum SearchOption {
    TITLE("title"),
    CONTENT("content"),
    TITLE_CONTENT("title_content"),
    TITLE_CONTENT_COMMENT("title_content_comment");

    private final String value;

    SearchOption(String value) {
        this.value = value;
    }
}
