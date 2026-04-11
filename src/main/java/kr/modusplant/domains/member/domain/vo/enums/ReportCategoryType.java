package kr.modusplant.domains.member.domain.vo.enums;

import lombok.Getter;

@Getter
public enum ReportCategoryType {
    PROPOSAL("proposal"),
    BUG_REPORT("bugReport");

    private final String value;

    ReportCategoryType(String value) {
        this.value = value;
    }
}
