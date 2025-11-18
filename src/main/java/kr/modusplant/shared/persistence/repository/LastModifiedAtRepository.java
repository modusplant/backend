package kr.modusplant.shared.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface LastModifiedAtRepository<T> {
    List<T> findByLastModifiedAt(LocalDateTime lastModifiedAt);
}
