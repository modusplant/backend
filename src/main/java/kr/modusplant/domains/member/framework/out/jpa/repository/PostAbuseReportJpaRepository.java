package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
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