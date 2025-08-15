package kr.modusplant.shared.persistence.repository.supers;

import java.util.Optional;
import java.util.UUID;

public interface UuidPrimaryKeyRepository<T> {
    Optional<T> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);
}
