package kr.modusplant.domains.term.usecase.port.repository;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermId;

public interface TermRepository {
    Term save(Term term);

    boolean isIdExist(TermId termId);

    void deleteById(TermId termId);
}
