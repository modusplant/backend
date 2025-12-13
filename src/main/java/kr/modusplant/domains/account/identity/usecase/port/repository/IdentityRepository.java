package kr.modusplant.domains.account.identity.usecase.port.repository;

import kr.modusplant.domains.account.identity.usecase.response.IdentityAuthResponse;
import kr.modusplant.domains.account.shared.kernel.AccountId;

public interface IdentityRepository {

    IdentityAuthResponse getAuthInfo(AccountId id);

}
