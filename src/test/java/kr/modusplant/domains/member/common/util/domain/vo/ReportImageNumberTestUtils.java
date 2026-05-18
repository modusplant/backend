package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportImageNumber;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_NUMBER;

public interface ReportImageNumberTestUtils {
    ReportImageNumber testReportImageNumber3 = ReportImageNumber.create(TEST_REPORT_IMAGE_NUMBER);
}
