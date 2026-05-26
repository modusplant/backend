package kr.modusplant.domains.member.common.util.framework.outbound.jooq.record;

import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.framework.outbound.jooq.record.ProposalOrBugReportAdminPageRecord;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ProposalOrBugReportAdminPageRecordTestUtils {
    ProposalOrBugReportAdminPageRecord testProposalOrBugReportAdminPageCheckedRecord =
            new ProposalOrBugReportAdminPageRecord(
                    TEST_REPORT_ULID,
                    TEST_REPORT_TITLE,
                    TEST_REPORT_CONTENT,
                    TEST_REPORT_IMAGE_JSONB,
                    TEST_REPORT_IMAGE_NUMBER_3,
                    TEST_REPORT_CHECKED_AT,
                    TEST_REPORT_CREATED_AT,
                    TEST_REPORT_CHECKED_AT,
                    MEMBER_BASIC_USER_NICKNAME,
                    ProposalOrBugReportStatus.CHECKED.getValue());
}
