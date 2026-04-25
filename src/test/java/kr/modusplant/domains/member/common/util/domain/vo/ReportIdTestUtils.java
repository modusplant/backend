package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportId;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;

public interface ReportIdTestUtils {
    ReportId testReportId = ReportId.create(TEST_REPORT_ULID);
}
