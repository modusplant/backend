package kr.modusplant.domains.term.adaptor.mapper;

import kr.modusplant.domains.member.usecase.request.TermCreateRequest;
import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import kr.modusplant.domains.term.usecase.port.mapper.TermMapper;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import org.springframework.stereotype.Component;

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
    public Term toTerm(TermCreateRequest request) {
        return Term.create(
                TermName.create(request.termName()),
                TermContent.create(request.termContent()),
                TermVersion.create(request.termVersion())
        );
    }
}
