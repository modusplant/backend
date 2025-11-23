package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPostBookmarkEntity;
import kr.modusplant.shared.persistence.compositekey.CommPostBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommPostBookmarkJpaRepository extends JpaRepository<CommPostBookmarkEntity, CommPostBookmarkId> {

    List<CommPostBookmarkEntity> findByMemberId(UUID memberId);

    List<CommPostBookmarkEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
