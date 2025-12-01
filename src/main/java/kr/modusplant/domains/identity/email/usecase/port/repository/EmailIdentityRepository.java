package kr.modusplant.domains.identity.email.usecase.port.repository;

import kr.modusplant.domains.identity.email.domain.vo.Email;
import kr.modusplant.domains.identity.email.domain.vo.Password;

public interface EmailIdentityRepository {

    boolean existsByEmailAndProvider(Email email);

    int updatePassword(Email email, Password pw);
}
