package kr.modusplant.domains.account.normal.usecase.port.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;

public interface NormalIdentityReadRepository {

    String getMemberPassword(AccountId accountId);

    AuthProvider getMemberAuthProvider(Email email);

    boolean existsByMemberId(AccountId accountId);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
