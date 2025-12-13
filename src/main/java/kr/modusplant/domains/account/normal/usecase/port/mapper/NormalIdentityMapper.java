package kr.modusplant.domains.account.normal.usecase.port.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;

public interface NormalIdentityMapper {
    SignUpData toSignUpData(NormalSignUpRequest request);
}
