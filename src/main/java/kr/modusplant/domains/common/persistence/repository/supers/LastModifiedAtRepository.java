package kr.modusplant.domains.common.persistence.repository.supers;

import java.time.LocalDateTime;
import java.util.List;

public interface LastModifiedAtRepository<T> {
    List<T> findByLastModifiedAt(LocalDateTime lastModifiedAt);
}
