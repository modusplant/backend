package kr.modusplant.domains.member.common.util.domain.entity;

import kr.modusplant.domains.member.domain.entity.ReportImage;

import java.util.List;

import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageBytesTestUtils.*;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageFileNameTestUtils.*;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.*;

public interface ReportImageTestUtils {
    ReportImage testReportImage1 = ReportImage.create(
            testReportImagePath1, testReportImageFileName1, testReportImageBytes1);
    List<ReportImage> testReportImages = List.of(
            ReportImage.create(testReportImagePath1, testReportImageFileName1, testReportImageBytes1),
            ReportImage.create(testReportImagePath2, testReportImageFileName2, testReportImageBytes2),
            ReportImage.create(testReportImagePath3, testReportImageFileName3, testReportImageBytes3));
}
