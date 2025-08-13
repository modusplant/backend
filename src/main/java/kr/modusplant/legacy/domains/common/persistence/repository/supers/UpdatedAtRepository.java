package kr.modusplant.legacy.domains.common.persistence.repository.supers;

import java.time.LocalDateTime;
import java.util.List;

public interface UpdatedAtRepository<T> {
    List<T> findByUpdatedAt(LocalDateTime updatedAt);
}
