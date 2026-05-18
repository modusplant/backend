package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportContent;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_CONTENT;

public interface ReportContentTestUtils {
    ReportContent testReportContent = ReportContent.create(TEST_REPORT_CONTENT);
}
