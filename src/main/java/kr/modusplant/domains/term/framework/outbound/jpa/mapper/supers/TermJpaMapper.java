package kr.modusplant.domains.term.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.TermEntity;

public interface TermJpaMapper {
    TermEntity toTermNewEntity(Term term);

    Term toTerm(TermEntity entity);
}
