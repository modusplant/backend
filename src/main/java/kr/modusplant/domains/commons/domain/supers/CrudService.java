package kr.modusplant.domains.commons.domain.supers;

import java.util.List;

public interface CrudService<T> {
    List<T> getAll();

    T insert(T object);

    T update(T object);
}