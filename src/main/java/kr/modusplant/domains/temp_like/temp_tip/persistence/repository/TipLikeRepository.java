package kr.modusplant.domains.temp_like.temp_tip.persistence.repository;

import kr.modusplant.domains.temp_like.temp_tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.temp_like.temp_tip.persistence.entity.TipLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipLikeRepository extends JpaRepository<TipLikeEntity, TipLikeId> {
}
