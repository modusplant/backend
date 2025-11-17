package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommCommentLikeEntity;
import kr.modusplant.shared.persistence.compositekey.CommCommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommCommentLikeJpaRepository extends JpaRepository<CommCommentLikeEntity, CommCommentLikeId> {

    List<CommCommentLikeEntity> findByMemberId(UUID memberId);

    boolean existsByPostIdAndPathAndMemberId(String postId, String path, UUID memberId);

    void deleteByPostIdAndPathAndMemberId(String postId, String path, UUID memberId);
}
