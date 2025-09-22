package kr.modusplant.domains.identity.adapter.mapper;

import kr.modusplant.domains.identity.domain.vo.SignUpData;
import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class NormalIdentityMapperImpl {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public SignUpData toSignUpData(NormalSignUpRequest request) {
        return SignUpData.create(request.email(), encoder.encode(request.password()), request.nickname(),
                request.agreedTermsOfUseVersion(), request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion());
    }
}
