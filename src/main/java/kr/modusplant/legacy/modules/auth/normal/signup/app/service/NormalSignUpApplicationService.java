package kr.modusplant.legacy.modules.auth.normal.signup.app.service;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberAuthApplicationService;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberTermApplicationService;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberAuthValidationService;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import kr.modusplant.legacy.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import kr.modusplant.legacy.modules.auth.normal.signup.mapper.NormalSignUpMemberAppDomainMapper;
import kr.modusplant.legacy.modules.auth.normal.signup.mapper.NormalSignupAuthAppDomainMapper;
import kr.modusplant.legacy.modules.auth.normal.signup.mapper.NormalSignupTermAppDomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NormalSignUpApplicationService {

    private final SiteMemberAuthValidationService siteMemberAuthValidationService;
    private final SiteMemberApplicationService siteMemberApplicationService;
    private final SiteMemberAuthApplicationService siteMemberAuthApplicationService;
    private final SiteMemberTermApplicationService siteMemberTermApplicationService;
    private final NormalSignUpMemberAppDomainMapper normalSignUpMemberAppDomainMapper;
    private final NormalSignupAuthAppDomainMapper normalSignupAuthAppDomainMapper;
    private final NormalSignupTermAppDomainMapper normalSignupTermAppDomainMapper;

    @Transactional
    public void insertMember(NormalSignUpRequest request) {
        siteMemberAuthValidationService.validateExistedEmailAndAuthProvider(request.email(), AuthProvider.BASIC);

        SiteMemberResponse savedMember = siteMemberApplicationService.insert(normalSignUpMemberAppDomainMapper.toSiteMemberInsertRequest(request));
        siteMemberAuthApplicationService.insert(normalSignupAuthAppDomainMapper.toSiteMemberAuthInsertRequest(request, savedMember));
        siteMemberTermApplicationService.insert(normalSignupTermAppDomainMapper.toSiteMemberTermInsertRequest(request, savedMember));
    }
}
