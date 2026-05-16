package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.ProposalBugReportEntity;

import static kr.modusplant.framework.jpa.entity.common.util.record.FilenameAndSrcEntityRecordTestUtils.testFilenameAndSrcEntityRecords;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;

public interface ProposalBugReportEntityTestUtils extends MemberEntityTestUtils {
    default ProposalBugReportEntity.ProposalBugReportEntityBuilder createProposalBugReportEntityBuilder() {
        return ProposalBugReportEntity.builder()
                .ulid(TEST_REPORT_ULID)
                .title(TEST_REPORT_TITLE)
                .content(TEST_REPORT_CONTENT)
                .image(testFilenameAndSrcEntityRecords)
                .imageNumber(TEST_REPORT_IMAGE_NUMBER);
    }
}
