package kr.modusplant.domains.normalidentity.normal.usecase.port.mapper;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.normalidentity.normal.usecase.request.NormalSignUpRequest;

public interface NormalIdentityMapper {
    SignUpData toSignUpData(NormalSignUpRequest request);
}
