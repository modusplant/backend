package kr.modusplant.domains.communication.tip.persistence.like.repository;

import kr.modusplant.domains.communication.tip.persistence.like.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.like.entity.TipLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TipLikeRepository extends JpaRepository<TipLikeEntity, TipLikeId> {
    boolean existsByTipPostIdAndMemberId(String tipPostId, UUID memberId);
}
