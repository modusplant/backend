package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.domain.service.crud.supers.UuidCrudService;

import java.util.Optional;

public interface TermService extends UuidCrudService<Term> {
    Optional<Term> getByName(String name);

    Optional<Term> getByVersion(String version);
}