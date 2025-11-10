package kr.modusplant.domains.normalidentity.normal.adapter.mapper;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.normalidentity.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.normalidentity.normal.usecase.request.NormalSignUpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityMapperImpl implements NormalIdentityMapper {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public SignUpData toSignUpData(NormalSignUpRequest request) {
        return SignUpData.create(request.email(), encoder.encode(request.password()), request.nickname(),
                request.agreedTermsOfUseVersion(), request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion());
    }
}
