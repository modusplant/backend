package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationCommentRepository;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.compositekey.TipCommentCompositeKey;
import org.springframework.stereotype.Repository;

@Repository
public interface TipCommentRepository extends CommunicationCommentRepository<TipCommentEntity, TipPostEntity, TipCommentCompositeKey> {
}
