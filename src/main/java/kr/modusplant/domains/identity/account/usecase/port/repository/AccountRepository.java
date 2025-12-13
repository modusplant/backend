package kr.modusplant.domains.identity.account.usecase.port.repository;

import kr.modusplant.domains.identity.account.domain.vo.AccountId;
import kr.modusplant.domains.identity.account.usecase.response.AccountAuthResponse;

public interface AccountRepository {

    AccountAuthResponse getAuthInfo(AccountId id);

}
