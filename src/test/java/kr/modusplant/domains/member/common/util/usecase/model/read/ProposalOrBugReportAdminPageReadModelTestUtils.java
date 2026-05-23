package kr.modusplant.domains.member.common.util.usecase.model.read;

import kr.modusplant.domains.member.domain.enums.ReportStatus;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ProposalOrBugReportAdminPageReadModelTestUtils {
    ProposalOrBugReportAdminPageReadModel testProposalOrBugReportAdminPageCheckedReadModel =
            new ProposalOrBugReportAdminPageReadModel(
                    TEST_REPORT_ULID,
                    TEST_REPORT_TITLE,
                    TEST_REPORT_CONTENT,
                    TEST_REPORT_IMAGE_BYTES_LIST,
                    TEST_REPORT_CHECKED_AT,
                    TEST_REPORT_CREATED_AT,
                    TEST_REPORT_CHECKED_AT,
                    MEMBER_BASIC_USER_NICKNAME,
                    ReportStatus.CHECKED.getValue());
}
