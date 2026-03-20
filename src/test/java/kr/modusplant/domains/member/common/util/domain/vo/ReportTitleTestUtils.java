package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportTitle;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_TITLE;

public interface ReportTitleTestUtils {
    ReportTitle testReportTitle = ReportTitle.create(TEST_REPORT_TITLE);
}
