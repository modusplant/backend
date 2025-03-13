package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.domain.service.crud.supers.CrudService;
import kr.modusplant.global.persistence.entity.TermEntity;

import java.util.Optional;

public interface TermService extends CrudService<Term> {
    Optional<TermEntity> getByName(String name);

    Optional<TermEntity> getByVersion(String version);
}