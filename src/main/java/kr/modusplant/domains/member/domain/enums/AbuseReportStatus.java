package kr.modusplant.domains.member.domain.enums;

import lombok.Getter;

@Getter
public enum AbuseReportStatus {
    UNCHECKED("미확인"),
    DISMISSED("반려함"),
    BLINDED("가려짐");

    private final String value;

    AbuseReportStatus(String value) {
        this.value = value;
    }
}
