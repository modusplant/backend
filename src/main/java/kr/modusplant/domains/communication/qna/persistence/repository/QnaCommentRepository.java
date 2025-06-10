package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationCommentRepository;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.compositekey.QnaCommentCompositeKey;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaCommentRepository extends CommunicationCommentRepository<QnaCommentEntity, QnaPostEntity, QnaCommentCompositeKey> {
}
