package kr.modusplant.global.domain.service.crud.supers;

import java.util.List;

public interface CrudService<T> {
    List<T> getAll();

    T insert(T object);

    T update(T object);
}