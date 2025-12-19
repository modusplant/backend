package kr.modusplant.domains.account.normal.usecase.port.repository;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;

public interface NormalIdentityCreateRepository {

    void save(SignUpData signUpData);
}
