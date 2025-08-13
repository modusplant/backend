package kr.modusplant.legacy.domains.common.persistence.repository.supers;

import java.time.LocalDateTime;
import java.util.List;

public interface CreatedAtRepository<T> {
    List<T> findByCreatedAt(LocalDateTime createdAt);
}
