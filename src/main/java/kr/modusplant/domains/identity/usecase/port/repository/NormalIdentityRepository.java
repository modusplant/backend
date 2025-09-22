package kr.modusplant.domains.identity.usecase.port.repository;

import kr.modusplant.domains.identity.domain.vo.SignUpData;

public interface NormalIdentityRepository {

    void save(SignUpData signUpData);
}
