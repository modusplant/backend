package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface TipCategoryRepository extends CommunicationCategoryRepository<TipCategoryEntity> {
}