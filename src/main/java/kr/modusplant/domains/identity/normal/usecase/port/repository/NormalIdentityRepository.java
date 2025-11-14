package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;

public interface NormalIdentityRepository {

    void save(SignUpData signUpData);

    boolean existsByEmailAndProvider(String email, String provider);

    boolean isNicknameExists(Nickname nickname);
}
