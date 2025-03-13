package kr.modusplant.global.persistence.repository.supers;

import java.time.LocalDateTime;
import java.util.List;

public interface LastModifiedAtRepository<T> {
    List<T> findByLastModifiedAt(LocalDateTime lastModifiedAt);
}
