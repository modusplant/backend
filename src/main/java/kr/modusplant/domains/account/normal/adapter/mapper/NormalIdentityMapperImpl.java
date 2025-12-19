package kr.modusplant.domains.account.normal.adapter.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityMapperImpl implements NormalIdentityMapper {

    private final PasswordEncoder encoder;

    public NormalIdentityMapperImpl(@Qualifier("bcryptPasswordEncoder") PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public SignUpData toSignUpData(NormalSignUpRequest request) {
        return SignUpData.create(request.email(), encoder.encode(request.password()), request.nickname(),
                request.agreedTermsOfUseVersion(), request.agreedPrivacyPolicyVersion(),
                request.agreedAdInfoReceivingVersion());
    }
}
