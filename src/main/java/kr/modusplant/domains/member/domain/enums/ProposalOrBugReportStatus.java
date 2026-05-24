package kr.modusplant.domains.member.domain.enums;

import lombok.Getter;

@Getter
public enum ProposalOrBugReportStatus {
    UNCHECKED("미확인"),
    CHECKED("확인함");

    private final String value;

    ProposalOrBugReportStatus(String value) {
        this.value = value;
    }
}
