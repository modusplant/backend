package kr.modusplant.shared.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface UpdatedAtRepository<T> {
    List<T> findByUpdatedAt(LocalDateTime updatedAt);
}
