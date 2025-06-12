package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface QnaCategoryRepository extends CommunicationCategoryRepository<QnaCategoryEntity> {
}