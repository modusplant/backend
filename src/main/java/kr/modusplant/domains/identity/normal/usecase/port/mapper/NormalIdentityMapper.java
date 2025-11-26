package kr.modusplant.domains.identity.normal.usecase.port.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;

public interface NormalIdentityMapper {
    SignUpData toSignUpData(NormalSignUpRequest request);
}
