package kr.modusplant.domains.identity.email.usecase.port.repository;

import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;

public interface EmailIdentityRepository {

    boolean existsByEmailAndProvider(Email email);

    int updatePassword(Email email, Password pw);
}
