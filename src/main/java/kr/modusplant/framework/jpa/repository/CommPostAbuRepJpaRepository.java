package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPostAbuRepEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommPostAbuRepJpaRepository extends
        CreatedAtAndLastModifiedAtRepository<CommPostAbuRepEntity>,
        JpaRepository<CommPostAbuRepEntity, UUID> {
    Optional<CommPostAbuRepEntity> findByMemberIdAndPost(UUID memberId, CommPostEntity post);
}