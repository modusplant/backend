package kr.modusplant.domains.member.domain.enums;

import lombok.Getter;

@Getter
public enum ReportStatus {
    UNCHECKED("미확인"),
    CHECKED("확인함"),
    HANDLED("처리됨");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }
}
