package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;

public interface NormalIdentityRepository {

    void save(SignUpData signUpData);
}
