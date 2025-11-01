package kr.modusplant.domains.term.adaptor.mapper;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.usecase.port.mapper.TermMapper;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TermMapperImpl implements TermMapper {
    @Override
    public TermResponse toTermResponse(Term term) {
        return new TermResponse(
                term.getTermId().getValue(),
                term.getTermName().getValue(),
                term.getTermContent().getValue(),
                term.getTermVersion().getValue()
        );
    }

    @Override
    public List<TermResponse> toTermListResponse(List<Term> termList) {
        return termList.stream()
                .map(this::toTermResponse)
                .toList();
    }
}
