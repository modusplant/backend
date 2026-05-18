package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostBookmarkEntity;
import kr.modusplant.shared.persistence.compositekey.PostBookmarkCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostBookmarkJpaRepository extends JpaRepository<PostBookmarkEntity, PostBookmarkCompositeKey> {

    List<PostBookmarkEntity> findByMemberId(UUID memberId);

    List<PostBookmarkEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByMemberId(UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostId(String postId);
}
