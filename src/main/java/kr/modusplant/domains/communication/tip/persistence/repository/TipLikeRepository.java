package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TipLikeRepository extends JpaRepository<TipLikeEntity, TipLikeId> {
    // 사용자별 팁 게시글 좋아요 리스트 조회
    List<TipLikeEntity> findByMemberIdAndTipPostIdIn(UUID memberId, List<String> tipPostIds);

    boolean existsByTipPostIdAndMemberId(String tipPostId, UUID memberId);
    void deleteByTipPostIdAndMemberId(String tipPostId, UUID memberId);
}
