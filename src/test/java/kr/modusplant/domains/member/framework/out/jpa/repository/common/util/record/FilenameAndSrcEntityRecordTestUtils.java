package kr.modusplant.domains.member.framework.out.jpa.repository.common.util.record;

import kr.modusplant.domains.member.framework.out.jpa.entity.record.FilenameAndSrcEntityRecord;

import java.util.List;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;

public interface FilenameAndSrcEntityRecordTestUtils {
    FilenameAndSrcEntityRecord testFilenameAndSrcEntityRecord1 =
            new FilenameAndSrcEntityRecord(TEST_REPORT_IMAGE_FILE_NAME_1, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1);
    FilenameAndSrcEntityRecord testFilenameAndSrcEntityRecord2 =
            new FilenameAndSrcEntityRecord(TEST_REPORT_IMAGE_FILE_NAME_2, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2);
    FilenameAndSrcEntityRecord testFilenameAndSrcEntityRecord3 =
            new FilenameAndSrcEntityRecord(TEST_REPORT_IMAGE_FILE_NAME_3, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3);
    List<FilenameAndSrcEntityRecord> testFilenameAndSrcEntityRecords =
            List.of(testFilenameAndSrcEntityRecord1, testFilenameAndSrcEntityRecord2, testFilenameAndSrcEntityRecord3);
}
