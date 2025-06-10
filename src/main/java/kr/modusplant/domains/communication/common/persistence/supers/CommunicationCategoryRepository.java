package kr.modusplant.domains.communication.common.persistence.supers;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface CommunicationCategoryRepository<T> extends UuidPrimaryKeyRepository<T>, CreatedAtRepository<T>, JpaRepository<T, UUID> {
    Optional<T> findByOrder(Integer order);

    Optional<T> findByCategory(String category);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
