package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;

public interface ProposalOrBugReportRemoveEventTestUtils {
    ProposalOrBugReportRemoveEvent testProposalOrBugReportRemoveEvent =
            ProposalOrBugReportRemoveEvent.create(TEST_REPORT_ULID);
}
