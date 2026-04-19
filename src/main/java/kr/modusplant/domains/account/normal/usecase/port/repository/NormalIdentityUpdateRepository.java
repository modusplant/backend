package kr.modusplant.domains.account.normal.usecase.port.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;

public interface NormalIdentityUpdateRepository {

    void updateEmail(AccountId accountId, Email email);

    void updatePassword(AccountId accountId, Password pw);

    void updateToGoogleAccount(Email email, Password pw);

    void updateToKakaoAccount(Email email, Password pw);

}
