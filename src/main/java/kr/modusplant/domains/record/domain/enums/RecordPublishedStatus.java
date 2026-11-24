package kr.modusplant.domains.record.domain.enums;

import lombok.Getter;

@Getter
public enum RecordPublishedStatus {
    PUBLISHED("PUBLISHED"),
    HIDDEN("HIDDEN"),
    BLOCKED("BLOCKED"),
    DELETED("DELETED");

    private final String value;

    RecordPublishedStatus(String value) {
        this.value = value;
    }
}
