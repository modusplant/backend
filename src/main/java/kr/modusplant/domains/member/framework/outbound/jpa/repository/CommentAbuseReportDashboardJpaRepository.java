package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.CommentAbuseReportDashboardCompositeKey;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportDashboardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentAbuseReportDashboardJpaRepository
        extends JpaRepository<CommentAbuseReportDashboardEntity, CommentAbuseReportDashboardCompositeKey> {
}
