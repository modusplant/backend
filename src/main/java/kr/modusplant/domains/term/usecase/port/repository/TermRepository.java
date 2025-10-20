package kr.modusplant.domains.term.usecase.port.repository;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermId;

import java.util.Optional;

public interface TermRepository {
    Term save(Term term);

    Optional<Term> findById(TermId termId);

    boolean isIdExist(TermId termId);

    void deleteById(TermId termId);
}
