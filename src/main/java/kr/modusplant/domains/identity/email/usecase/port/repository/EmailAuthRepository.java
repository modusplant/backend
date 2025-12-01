package kr.modusplant.domains.identity.email.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.Password;

public interface EmailAuthRepository {

    boolean existsByEmailAndProvider(Email email);

    int updatePassword(Email email, Password pw);
}
