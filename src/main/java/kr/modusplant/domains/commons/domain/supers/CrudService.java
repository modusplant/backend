<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/commons/domain/supers/CrudService.java
package kr.modusplant.domains.commons.domain.supers;
========
package kr.modusplant.api.crud.common.domain.supers;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/common/domain/supers/CrudService.java

import java.util.List;

public interface CrudService<T> {
    List<T> getAll();

    T insert(T object);

    T update(T object);
}