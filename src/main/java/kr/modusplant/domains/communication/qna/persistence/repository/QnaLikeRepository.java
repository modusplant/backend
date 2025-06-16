package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeId;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaLikeRepository extends CommunicationLikeRepository<QnaLikeEntity, QnaLikeId> {
}
