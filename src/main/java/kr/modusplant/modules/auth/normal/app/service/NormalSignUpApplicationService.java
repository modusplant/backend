package kr.modusplant.modules.auth.normal.app.service;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberAuthApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberTermApplicationService;
import kr.modusplant.domains.member.domain.service.SiteMemberAuthValidationService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import kr.modusplant.modules.auth.normal.mapper.NormalSignUpMemberAppDomainMapper;
import kr.modusplant.modules.auth.normal.mapper.NormalSignupAuthAppDomainMapper;
import kr.modusplant.modules.auth.normal.mapper.NormalSignupTermAppDomainMapper;
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
