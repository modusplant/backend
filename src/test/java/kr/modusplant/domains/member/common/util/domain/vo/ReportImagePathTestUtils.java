package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportImagePath;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.REPORT_IMAGE_PATH;

public interface ReportImagePathTestUtils {
    ReportImagePath testReportImagePath = ReportImagePath.create(REPORT_IMAGE_PATH);
}
