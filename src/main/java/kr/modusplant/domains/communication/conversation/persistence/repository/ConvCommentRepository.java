package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationCommentRepository;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvCommentRepository extends CommunicationCommentRepository<ConvCommentEntity, ConvPostEntity, ConvCommentCompositeKey> {
}
