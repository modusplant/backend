package kr.modusplant.modules.auth.normal.signup.app.service;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberAuthApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberTermApplicationService;
import kr.modusplant.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import kr.modusplant.modules.auth.normal.signup.mapper.NormalSignUpMemberAppDomainMapper;
import kr.modusplant.modules.auth.normal.signup.mapper.NormalSignupAuthAppDomainMapper;
import kr.modusplant.modules.auth.normal.signup.mapper.NormalSignupTermAppDomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NormalSignUpApplicationService {

    private final SiteMemberApplicationService siteMemberApplicationService;
    private final SiteMemberAuthApplicationService siteMemberAuthApplicationService;
    private final SiteMemberTermApplicationService siteMemberTermApplicationService;
    private final NormalSignUpMemberAppDomainMapper normalSignUpMemberAppDomainMapper;
    private final NormalSignupAuthAppDomainMapper normalSignupAuthAppDomainMapper;
    private final NormalSignupTermAppDomainMapper normalSignupTermAppDomainMapper;

    @Transactional
    public void insertMember(NormalSignUpRequest request) {
        SiteMemberResponse savedMember = siteMemberApplicationService.insert(normalSignUpMemberAppDomainMapper.toSiteMemberInsertRequest(request));
        siteMemberAuthApplicationService.insert(normalSignupAuthAppDomainMapper.toSiteMemberAuthInsertRequest(request, savedMember));
        siteMemberTermApplicationService.insert(normalSignupTermAppDomainMapper.toSiteMemberTermInsertRequest(request, savedMember));
    }
}
