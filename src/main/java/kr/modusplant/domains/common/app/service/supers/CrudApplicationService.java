package kr.modusplant.domains.common.app.service.supers;

import java.util.List;

public interface CrudApplicationService<T, I, U> {
    List<T> getAll();

    T insert(I object);

    T update(U object);
}