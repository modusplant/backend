package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeId;
import org.springframework.stereotype.Repository;

@Repository
public interface TipLikeRepository extends CommunicationLikeRepository<TipLikeEntity, TipLikeId> {
}
