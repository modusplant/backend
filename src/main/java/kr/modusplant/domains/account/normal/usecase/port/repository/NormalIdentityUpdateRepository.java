package kr.modusplant.domains.account.normal.usecase.port.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;

public interface NormalIdentityUpdateRepository {

    int updateEmail(AccountId accountId, Email email);

    int updatePassword(AccountId accountId, Password pw);

}
