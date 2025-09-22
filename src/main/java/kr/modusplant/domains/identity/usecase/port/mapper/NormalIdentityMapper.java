package kr.modusplant.domains.identity.usecase.port.mapper;

import kr.modusplant.domains.identity.domain.vo.SignUpData;
import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;

public interface NormalIdentityMapper {
    SignUpData toSignUpData(NormalSignUpRequest request);
}
