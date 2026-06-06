package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;

import static kr.modusplant.domains.member.common.constant.ReportConstant.*;
import static kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.record.FilenameAndSrcEntityRecordTestUtils.testFilenameAndSrcEntityRecords;

public interface ProposalBugReportEntityTestUtils extends MemberEntityTestUtils {
    default ProposalOrBugReportEntity.ProposalOrBugReportEntityBuilder createProposalBugReportEntityBuilder() {
        return ProposalOrBugReportEntity.builder()
                .ulid(TEST_REPORT_ULID)
                .title(TEST_REPORT_TITLE)
                .content(TEST_REPORT_CONTENT)
                .image(testFilenameAndSrcEntityRecords)
                .imageNumber(TEST_REPORT_IMAGE_NUMBER_3);
    }
}
