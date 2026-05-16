package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommentLikeEntity;
import kr.modusplant.shared.persistence.compositekey.CommentLikeCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeEntity, CommentLikeCompositeKey> {

    List<CommentLikeEntity> findByMemberId(UUID memberId);

    boolean existsByPostIdAndPathAndMemberId(String postId, String path, UUID memberId);

    void deleteByMemberId(UUID memberId);

    void deleteByPostIdAndPathAndMemberId(String postId, String path, UUID memberId);
}
