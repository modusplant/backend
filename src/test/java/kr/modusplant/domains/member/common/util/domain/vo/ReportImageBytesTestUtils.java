package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportImageBytes;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;

public interface ReportImageBytesTestUtils {
    ReportImageBytes testReportImageBytes1 = ReportImageBytes.create(TEST_REPORT_IMAGE_BYTES_1);
    ReportImageBytes testReportImageBytes2 = ReportImageBytes.create(TEST_REPORT_IMAGE_BYTES_2);
    ReportImageBytes testReportImageBytes3 = ReportImageBytes.create(TEST_REPORT_IMAGE_BYTES_3);
}
