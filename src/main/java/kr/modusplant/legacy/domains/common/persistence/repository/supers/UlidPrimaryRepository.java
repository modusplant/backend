package kr.modusplant.legacy.domains.common.persistence.repository.supers;

import java.util.Optional;

public interface UlidPrimaryRepository<T> {
    Optional<T> findByUlid(String ulid);

    void deleteByUlid(String ulid);

    boolean existsByUlid(String ulid);
}
