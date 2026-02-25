package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportImageBytes;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.REPORT_IMAGE_BYTES;

public interface ReportImageBytesTestUtils {
    ReportImageBytes testReportImageBytes = ReportImageBytes.create(REPORT_IMAGE_BYTES);
}
