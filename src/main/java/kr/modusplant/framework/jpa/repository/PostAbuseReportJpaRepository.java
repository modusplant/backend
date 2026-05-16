package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface PostAbuseReportJpaRepository extends
        CreatedAtAndLastModifiedAtRepository<PostAbuseReportEntity>,
        JpaRepository<PostAbuseReportEntity, UUID> {
    Optional<PostAbuseReportEntity> findByMemberIdAndPost(UUID memberId, PostEntity post);
}