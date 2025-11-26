package kr.modusplant.shared.persistence.repository;

import java.util.Optional;

public interface UlidPrimaryRepository<T> {
    Optional<T> findByUlid(String ulid);

    void deleteByUlid(String ulid);

    boolean existsByUlid(String ulid);
}
