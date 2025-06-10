package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeId;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvLikeRepository extends CommunicationLikeRepository<ConvLikeEntity, ConvLikeId> {
}
