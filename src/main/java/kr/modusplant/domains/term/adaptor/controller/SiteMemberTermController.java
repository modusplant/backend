package kr.modusplant.domains.term.adaptor.controller;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.domain.exception.AlreadySiteMemberTermException;
import kr.modusplant.domains.term.domain.exception.SiteMemberNotFoundException;
import kr.modusplant.domains.term.domain.exception.SiteMemberTermNotFoundException;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import kr.modusplant.domains.term.usecase.port.mapper.SiteMemberTermMapper;
import kr.modusplant.domains.term.usecase.port.repository.SiteMemberTermRepository;
import kr.modusplant.domains.term.usecase.request.SiteMemberTermCreateRequest;
import kr.modusplant.domains.term.usecase.request.SiteMemberTermUpdateRequest;
import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class SiteMemberTermController {
    private final SiteMemberTermMapper mapper;
    private final SiteMemberTermRepository siteMemberTermRepository;

    public SiteMemberTermResponse register(SiteMemberTermCreateRequest request) {
        if (siteMemberTermRepository.isIdExist(SiteMemberTermId.fromUuid(request.uuid())))
            throw new AlreadySiteMemberTermException();

        SiteMemberTerm siteMemberTerm = SiteMemberTerm.create(
                SiteMemberTermId.fromUuid(request.uuid()),
                request.agreedTermsOfUseVersion(),
                request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion()
        );
        return mapper.toSiteMemberTermResponse(siteMemberTermRepository.save(siteMemberTerm));
    }

    public SiteMemberTermResponse update(SiteMemberTermUpdateRequest request) {
        SiteMemberTerm siteMemberTerm = siteMemberTermRepository.findById(SiteMemberTermId.fromUuid(request.uuid()))
                .orElseThrow(SiteMemberNotFoundException::new);

        SiteMemberTerm updateSiteMemberTerm = siteMemberTerm.create(
                request.agreedTermsOfUseVersion(),
                request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion()
        );
        return mapper.toSiteMemberTermResponse(siteMemberTermRepository.save(updateSiteMemberTerm));
    }

    public void delete(SiteMemberTermId siteMemberTermId) {
        siteMemberTermRepository.deleteById(siteMemberTermId);
    }

    public SiteMemberTermResponse getSiteMemberTerm(SiteMemberTermId siteMemberTermId) {
        SiteMemberTerm siteMemberTerm = siteMemberTermRepository.findById(siteMemberTermId)
                .orElseThrow(SiteMemberTermNotFoundException::new);
        return mapper.toSiteMemberTermResponse(siteMemberTerm);
    }

    public List<SiteMemberTermResponse> getSiteMemberTermList() {
        List<SiteMemberTerm> responses = siteMemberTermRepository.findAll();
        return mapper.toSiteMemberTermListResponse(responses);
    }
}
