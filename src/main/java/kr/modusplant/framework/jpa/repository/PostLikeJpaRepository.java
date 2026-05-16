package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PostLikeEntity;
import kr.modusplant.shared.persistence.compositekey.PostLikeCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, PostLikeCompositeKey> {

    List<PostLikeEntity> findByMemberId(UUID memberId);

    List<PostLikeEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByMemberId(UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostId(String postId);
}
