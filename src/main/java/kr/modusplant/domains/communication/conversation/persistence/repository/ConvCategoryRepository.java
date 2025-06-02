package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface ConvCategoryRepository extends UuidPrimaryKeyRepository<ConvCategoryEntity>, CreatedAtRepository<ConvCategoryEntity>, JpaRepository<ConvCategoryEntity, Integer> {
    Optional<ConvCategoryEntity> findByOrder(Integer order);

    Optional<ConvCategoryEntity> findByCategory(String category);
}