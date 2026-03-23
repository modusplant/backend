package kr.modusplant.domains.post.usecase.enums;

import lombok.Getter;

@Getter
public enum SearchSort {
    LATEST("latest"),
    RELEVANCE("relevance");

    private final String value;

    SearchSort(String value) {
        this.value = value;
    }
}
