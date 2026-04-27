package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.domain.aggregate.Report;

import static kr.modusplant.domains.member.common.util.domain.entity.ReportImageTestUtils.testReportImages;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportContentTestUtils.testReportContent;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageNumberTestUtils.testReportImageNumber3;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTitleTestUtils.testReportTitle;

public interface ReportTestUtils {
    default Report createReport() {
        return Report.create(testReportId, testReportTitle, testReportContent, testReportImages, testReportImageNumber3);
    }
}
