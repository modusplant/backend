package kr.modusplant.domains.term.usecase.port.mapper;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.usecase.response.TermResponse;

import java.util.List;

public interface TermMapper {
    TermResponse toTermResponse(Term term);
    List<TermResponse> toTermListResponse(List<Term> termList);
}
