package kr.modusplant.domains.term.adaptor.controller;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.member.usecase.request.TermCreateRequest;
import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.usecase.port.mapper.TermMapper;
import kr.modusplant.domains.term.usecase.port.repository.TermRepository;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class TermController {
    private final TermMapper mapper;
    private final TermRepository termRepository;

    public TermResponse register(TermCreateRequest request) {
        Term term = mapper.toTerm(request);
        validateBeforeRegister(request);
        return mapper.toTermResponse(termRepository.save(term));
    }

    public void delete(TermId termId) {
        termRepository.deleteById(termId);
    }

    private void validateBeforeRegister(TermCreateRequest request) {
        // TODO: 버전 validation(ex : 1.0.1 -> 1.0.1 이상으로 입력)
    }
}
