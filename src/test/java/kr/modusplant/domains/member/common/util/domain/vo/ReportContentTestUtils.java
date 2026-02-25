package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportContent;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.REPORT_CONTENT;

public interface ReportContentTestUtils {
    ReportContent testReportContent = ReportContent.create(REPORT_CONTENT);
}
