package kr.modusplant.domains.account.normal.adapter.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalIdentityMapperImpl implements NormalIdentityMapper {

    private final PasswordEncoder encoder;

    @Override
    public SignUpData toSignUpData(NormalSignUpRequest request) {
        return SignUpData.create(request.email(), encoder.encode(request.password()), request.nickname(),
                request.agreedTermsOfUseVersion(), request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion());
    }
}
