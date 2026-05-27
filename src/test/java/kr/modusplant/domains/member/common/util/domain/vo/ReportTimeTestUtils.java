package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportTime;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;

public interface ReportTimeTestUtils {
    ReportTime testReportTime = ReportTime.create(TEST_POST_PUBLISHED_AT);
}
