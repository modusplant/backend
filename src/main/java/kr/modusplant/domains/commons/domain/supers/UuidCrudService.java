<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/commons/domain/supers/UuidCrudService.java
package kr.modusplant.domains.commons.domain.supers;
========
package kr.modusplant.api.crud.common.domain.supers;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/common/domain/supers/UuidCrudService.java

import java.util.Optional;
import java.util.UUID;

public interface UuidCrudService<T> extends CrudService<T> {
    Optional<T> getByUuid(UUID uuid);

    void removeByUuid(UUID uuid);
}
