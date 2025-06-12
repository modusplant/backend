package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface ConvCategoryRepository extends CommunicationCategoryRepository<ConvCategoryEntity> {
}