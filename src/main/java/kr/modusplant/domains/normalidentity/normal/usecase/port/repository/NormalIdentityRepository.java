package kr.modusplant.domains.normalidentity.normal.usecase.port.repository;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;

public interface NormalIdentityRepository {

    void save(SignUpData signUpData);

    boolean existsByEmailAndProvider(String email, String provider);
}
