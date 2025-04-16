package kr.modusplant.domains.term.domain.service.supers;

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.domains.term.domain.model.Term;

import java.util.List;
import java.util.Optional;

public interface TermCrudService extends UuidCrudService<Term> {
    List<Term> getByVersion(String version);

    Optional<Term> getByName(String name);
}