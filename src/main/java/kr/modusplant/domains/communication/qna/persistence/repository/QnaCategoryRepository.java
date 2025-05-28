package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface QnaCategoryRepository extends CreatedAtRepository<QnaCategoryEntity>, JpaRepository<QnaCategoryEntity, Integer> {
    Optional<QnaCategoryEntity> findByOrder(Integer order);

    Optional<QnaCategoryEntity> findByCategory(String category);

    void deleteByOrder(Integer order);

    boolean existsByOrder(Integer order);
}