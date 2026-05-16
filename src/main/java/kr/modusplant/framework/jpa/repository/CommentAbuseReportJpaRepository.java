package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.framework.jpa.entity.CommentEntity;
import kr.modusplant.shared.persistence.compositekey.CommentCompositeKey;
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