package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommentAbuseReportJpaRepository extends
        CreatedAtAndLastModifiedAtRepository<CommentAbuseReportEntity>,
        JpaRepository<CommentAbuseReportEntity, CommentCompositeKey> {
    Optional<CommentAbuseReportEntity> findByMemberIdAndComment(UUID memberId, CommentEntity comment);
}