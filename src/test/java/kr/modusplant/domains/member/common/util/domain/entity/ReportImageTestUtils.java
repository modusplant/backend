package kr.modusplant.domains.member.common.util.domain.entity;

import kr.modusplant.domains.member.domain.entity.ProposalOrBugReportImage;

import java.util.List;

import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageBytesTestUtils.*;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageFileNameTestUtils.*;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.*;

public interface ReportImageTestUtils {
    ProposalOrBugReportImage testProposalOrBugReportImage1 = ProposalOrBugReportImage.create(
            testReportImagePath1, testProposalOrBugReportImageFileName1, testReportImageBytes1);
    List<ProposalOrBugReportImage> testProposalOrBugReportImages = List.of(
            ProposalOrBugReportImage.create(testReportImagePath1, testProposalOrBugReportImageFileName1, testReportImageBytes1),
            ProposalOrBugReportImage.create(testReportImagePath2, testProposalOrBugReportImageFileName2, testReportImageBytes2),
            ProposalOrBugReportImage.create(testReportImagePath3, testProposalOrBugReportImageFileName3, testReportImageBytes3));
}
