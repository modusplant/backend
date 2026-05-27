package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportDashboardRepositoryAdapter implements ReportDashboardRepository {
    private final PostJpaRepository postJpaRepository;
    private final PostAbuseReportDashboardJpaRepository postAbuseReportDashboardJpaRepository;

    @Override
    public void addPostAbuseReport(ActivitySubjectPostId postIdVO, ReportTime reportTimeVO) {
        String postId = postIdVO.getValue();
        LocalDateTime reportTime = reportTimeVO.getValue();
        Optional<PostAbuseReportDashboardEntity> optionalDashboardEntity =
                postAbuseReportDashboardJpaRepository.findById(postId);
        if (optionalDashboardEntity.isEmpty()) {
            postAbuseReportDashboardJpaRepository.save(
                    PostAbuseReportDashboardEntity.builder()
                            .post(postJpaRepository.findByUlid(postId).orElseThrow())
                            .firstReportedAt(reportTime)
                            .lastReportedAt(reportTime)
                            .build()
            );
        } else {
            PostAbuseReportDashboardEntity dashboardEntity =
                    postAbuseReportDashboardJpaRepository.findById(postId).orElseThrow();
            dashboardEntity.increaseReportCount();
            dashboardEntity.updateLastReportedAt(reportTime);
            postAbuseReportDashboardJpaRepository.save(dashboardEntity);
        }
    }
}
