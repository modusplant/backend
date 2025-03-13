package kr.modusplant.global.domain.service.crud.supers;

import java.util.Optional;
import java.util.UUID;

public interface UuidCrudService<T> extends CrudService<T> {
    Optional<T> getByUuid(UUID uuid);

    void removeByUuid(UUID uuid);
}
