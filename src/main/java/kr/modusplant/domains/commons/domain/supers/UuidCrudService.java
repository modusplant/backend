package kr.modusplant.domains.commons.domain.supers;

import java.util.Optional;
import java.util.UUID;

public interface UuidCrudService<T> extends CrudService<T> {
    Optional<T> getByUuid(UUID uuid);

    void removeByUuid(UUID uuid);
}
