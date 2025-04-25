package kr.modusplant.modules.signup.normal.app.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberTermCrudService;
import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import kr.modusplant.modules.signup.normal.mapper.domain.SiteMemberAuthDomainMapper;
import kr.modusplant.modules.signup.normal.mapper.domain.SiteMemberDomainMapper;
import kr.modusplant.modules.signup.normal.mapper.domain.SiteMemberTermDomainMapper;
import kr.modusplant.modules.signup.normal.app.http.request.NormalSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NormalSignUpApplicationService {

    private final TermCrudService termCrudService;
    private final SiteMemberCrudService siteMemberCrudService;
    private final SiteMemberAuthCrudService siteMemberAuthCrudService;
    private final SiteMemberTermCrudService siteMemberTermCrudService;
    private final SiteMemberDomainMapper siteMemberDomainMapper;
    private final SiteMemberAuthDomainMapper siteMemberAuthDomainMapper;
    private final SiteMemberTermDomainMapper siteMemberTermDomainMapper;

    public List<Term> getAllTerms() {
        return termCrudService.getAll();
    }

    public List<Map<String, Object>> createTermMapList(List<Term> terms) {

        return terms.stream()
                .filter(term -> {
                    String termKey = term.getName();

                    return termKey.equals("이용약관") ||
                            termKey.equals("개인정보처리방침") ||
                            termKey.equals("광고성 정보 수신");
                })
                .map(this::createTermMap)
                .toList();
    }

    @Transactional
    public void insertMember(NormalSignUpRequest request) {
        SiteMember savedMember = siteMemberCrudService.insert(siteMemberDomainMapper.toSiteMember(request));
        siteMemberAuthCrudService.insert(siteMemberAuthDomainMapper.toSiteMemberAuth(request, savedMember));
        siteMemberTermCrudService.insert(siteMemberTermDomainMapper.toSiteMemberTerm(request, savedMember));
    }

    private Map<String, Object> createTermMap(Term term) {
        String mapKey = switch (term.getName()) {
            case ("개인정보처리방침") -> "privacyPolicy";
            case ("이용약관") -> "termsOfUse";
            case ("광고성 정보 수신") -> "adInfoReceiving";
            default -> "unKnownTerm";
        };

        return Map.of(mapKey, term);
    }
}
