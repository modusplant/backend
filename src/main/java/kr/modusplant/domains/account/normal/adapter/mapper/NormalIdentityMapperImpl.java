package kr.modusplant.domains.account.normal.adapter.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityMapperImpl implements NormalIdentityMapper {

    @Override
    public SignUpData toSignUpData(NormalSignUpRequest request) {
        return SignUpData.create(request.email(), request.password(), request.nickname(),
                request.agreedTermsOfUseVersion(), request.agreedPrivacyPolicyVersion(),
                request.agreedCommunityPolicyVersion());
    }
}
