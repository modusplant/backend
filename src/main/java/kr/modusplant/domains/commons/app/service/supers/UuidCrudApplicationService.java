package kr.modusplant.domains.commons.app.service.supers;

import java.util.Optional;
import java.util.UUID;

public interface UuidCrudApplicationService<T, I, U> extends CrudApplicationService<T, I, U> {
    Optional<T> getByUuid(UUID uuid);

    void removeByUuid(UUID uuid);
}
