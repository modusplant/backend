package kr.modusplant.domains.identity.adapter.repository;

import kr.modusplant.domains.identity.domain.vo.SignUpData;

public interface NormalIdentityRepository {

    void save(SignUpData signUpData);
}
