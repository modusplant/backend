package kr.modusplant.domains.account.email.usecase.port.repository;

import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;

import java.util.Optional;

public interface EmailIdentityRepository {

    boolean existsByEmailAndProvider(Email email, AuthProvider provider);

    int updatePassword(Email email, Password pw);

    Optional<AuthProvider> findProviderByEmail(Email email);
}
