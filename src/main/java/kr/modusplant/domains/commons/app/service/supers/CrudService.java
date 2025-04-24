package kr.modusplant.domains.commons.app.service.supers;

import java.util.List;

public interface CrudService<T> {
    List<T> getAll();

    T insert(T object);

    T update(T object);
}