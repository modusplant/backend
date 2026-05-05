package kr.modusplant.domains.search.domain.enums;

import lombok.Getter;

@Getter
public enum SearchPostSortCondition {
    LATEST("latest"),
    RELEVANCE("relevance");

    private final String value;

    SearchPostSortCondition(String value) {
        this.value = value;
    }
}
