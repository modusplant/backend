package kr.modusplant.domains.term.adaptor.controller;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.exception.TermNotFoundException;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import kr.modusplant.domains.term.usecase.port.mapper.TermMapper;
import kr.modusplant.domains.term.usecase.port.repository.TermRepository;
import kr.modusplant.domains.term.usecase.request.TermCreateRequest;
import kr.modusplant.domains.term.usecase.request.TermUpdateRequest;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TermController {
    private final TermMapper mapper;
    private final TermRepository termRepository;

    public TermResponse register(TermCreateRequest request) {
        validateBeforeRegister(request);
        Term term = Term.create(
                TermName.create(request.termName()),
                TermContent.create(request.termContent()),
                TermVersion.create(request.termVersion())
        );
        return mapper.toTermResponse(termRepository.save(term));
    }

    public TermResponse update(TermUpdateRequest request) {
        validateBeforeUpdate(request);
        Term term = termRepository.findById(TermId.fromUuid(request.termId()))
                .orElseThrow(TermNotFoundException::new);

        Term updateTerm = term.create(
                TermContent.create(request.termContent())
        );
        return mapper.toTermResponse(termRepository.save(updateTerm));
    }

    public void delete(TermId termId) {
        termRepository.deleteById(termId);
    }

    public TermResponse getTerm(TermId termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(TermNotFoundException::new);
        return mapper.toTermResponse(term);
    }

    public List<TermResponse> getTermList() {
        List<Term> responses = termRepository.findAll();
        return mapper.toTermListResponse(responses);
    }

    // TODO: 버전 validation(ex : 1.0.1 -> 1.0.1 이상으로 입력)
    private void validateBeforeRegister(TermCreateRequest request) {
    }

    private void validateBeforeUpdate(TermUpdateRequest request) {
    }
}
