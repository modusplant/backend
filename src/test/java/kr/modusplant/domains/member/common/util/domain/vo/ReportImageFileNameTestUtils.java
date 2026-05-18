package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportImageFileName;

import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ReportImageFileNameTestUtils {
    ReportImageFileName testReportImageFileName1 = ReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_1);
    ReportImageFileName testReportImageFileName2 = ReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_2);
    ReportImageFileName testReportImageFileName3 = ReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_3);
}
