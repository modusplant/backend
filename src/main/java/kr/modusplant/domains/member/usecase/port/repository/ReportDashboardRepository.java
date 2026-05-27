package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportTime;

public interface ReportDashboardRepository {
    void addPostAbuseReport(ActivitySubjectPostId postId, ReportTime reportTime);
}
