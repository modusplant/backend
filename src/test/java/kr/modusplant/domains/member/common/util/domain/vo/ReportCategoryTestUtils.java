package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportCategory;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_CATEGORY_BUG_REPORT;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_CATEGORY_PROPOSAL;

public interface ReportCategoryTestUtils {
    ReportCategory testReportCategoryProposal = ReportCategory.create(TEST_REPORT_CATEGORY_PROPOSAL);
    ReportCategory testReportCategoryBugReport = ReportCategory.create(TEST_REPORT_CATEGORY_BUG_REPORT);
}
