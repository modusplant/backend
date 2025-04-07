package kr.modusplant.api.crud.term.domain.service;

import kr.modusplant.api.crud.common.domain.supers.UuidCrudService;
import kr.modusplant.api.crud.term.domain.model.Term;

import java.util.List;
import java.util.Optional;

public interface TermService extends UuidCrudService<Term> {
    List<Term> getByVersion(String version);

    Optional<Term> getByName(String name);
}