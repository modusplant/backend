package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.entity.compositekey.CommPostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommPostLikeRepository extends JpaRepository<CommPostLikeEntity, CommPostLikeId> {

    List<CommPostLikeEntity> findByMemberId(UUID memberId);

    List<CommPostLikeEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
