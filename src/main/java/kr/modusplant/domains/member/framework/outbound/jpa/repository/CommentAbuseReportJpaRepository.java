package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommentAbuseReportJpaRepository extends
        CreatedAtRepository<CommentAbuseReportEntity>,
        JpaRepository<CommentAbuseReportEntity, CommentCompositeKey> {
    Optional<CommentAbuseReportEntity> findByMemberIdAndComment(UUID memberId, CommentEntity comment);
}