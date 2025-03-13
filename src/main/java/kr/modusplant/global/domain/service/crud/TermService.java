package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.domain.service.crud.supers.UuidCrudService;

import java.util.List;
import java.util.Optional;

public interface TermService extends UuidCrudService<Term> {
    List<Term> getByVersion(String version);

    Optional<Term> getByName(String name);
}