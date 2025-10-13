package kr.modusplant.domains.term.usecase.port.mapper;

import kr.modusplant.domains.member.usecase.request.TermCreateRequest;
import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.usecase.response.TermResponse;

public interface TermMapper {
    TermResponse toTermResponse(Term term);
    Term toTerm(TermCreateRequest request);
}
