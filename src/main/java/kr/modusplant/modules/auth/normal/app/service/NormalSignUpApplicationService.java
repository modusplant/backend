package kr.modusplant.modules.auth.normal.app.service;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberAuthApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberTermApplicationService;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import kr.modusplant.modules.auth.normal.mapper.NormalSignUpMemberAppDomainMapper;
import kr.modusplant.modules.auth.normal.mapper.NormalSignupAuthAppDomainMapper;
import kr.modusplant.modules.auth.normal.mapper.NormalSignupTermAppDomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NormalSignUpApplicationService {

    private final TermApplicationService termApplicationService;
    private final SiteMemberApplicationService siteMemberApplicationService;
    private final SiteMemberAuthApplicationService siteMemberAuthApplicationService;
    private final SiteMemberTermApplicationService siteMemberTermApplicationService;
    private final NormalSignUpMemberAppDomainMapper normalSignUpMemberAppDomainMapper;
    private final NormalSignupAuthAppDomainMapper normalSignupAuthAppDomainMapper;
    private final NormalSignupTermAppDomainMapper normalSignupTermAppDomainMapper;

    public List<TermResponse> getAllTerms() {
        return termApplicationService.getAll();
    }

    public List<Map<String, Object>> createTermMapList(List<TermResponse> terms) {

        return terms.stream()
                .filter(term -> {
                    String termKey = term.name();

                    return termKey.equals("이용약관") ||
                            termKey.equals("개인정보처리방침") ||
                            termKey.equals("광고성 정보 수신");
                })
                .map(this::createTermMap)
                .toList();
    }

    @Transactional
    public void insertMember(NormalSignUpRequest request) {
        SiteMemberResponse savedMember = siteMemberApplicationService.insert(normalSignUpMemberAppDomainMapper.toSiteMemberInsertRequest(request));
        siteMemberAuthApplicationService.insert(normalSignupAuthAppDomainMapper.toSiteMemberAuthInsertRequest(request, savedMember));
        siteMemberTermApplicationService.insert(normalSignupTermAppDomainMapper.toSiteMemberTermInsertRequest(request, savedMember));
    }

    private Map<String, Object> createTermMap(TermResponse term) {
        String mapKey = switch (term.name()) {
            case ("개인정보처리방침") -> "privacyPolicy";
            case ("이용약관") -> "termsOfUse";
            case ("광고성 정보 수신") -> "adInfoReceiving";
            default -> "unKnownTerm";
        };

        return Map.of(mapKey, term);
    }
}
