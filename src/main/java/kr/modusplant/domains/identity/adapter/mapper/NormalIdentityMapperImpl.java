package kr.modusplant.domains.identity.adapter.mapper;

import kr.modusplant.domains.identity.domain.vo.SignUpData;
import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;

public class NormalIdentityMapperImpl {

    public SignUpData toSignUpData(NormalSignUpRequest request) {
        return SignUpData.create(request.email(), request.password(), request.nickname(),
                request.agreedTermsOfUseVersion(), request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion());
    }
}
