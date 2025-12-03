package kr.modusplant.domains.identity.email.usecase.port.repository;

import kr.modusplant.domains.identity.email.domain.vo.Password;
import kr.modusplant.shared.kernel.Email;

public interface EmailIdentityRepository {

    boolean existsByEmailAndProvider(Email email);

    int updatePassword(Email email, Password pw);
}
