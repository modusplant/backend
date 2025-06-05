package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TipLikeRepository extends JpaRepository<TipLikeEntity, TipLikeId> {
    // 사용자별 팁 게시글 좋아요 전체 리스트 조회
    List<TipLikeEntity> findByMemberId(UUID memberId);

    // 사용자별 팁 게시글 좋아요 리스트 조회
    List<TipLikeEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);
    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
