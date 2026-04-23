package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.shared.enums.Platform;

public interface FcmTokenRepository {
    void saveOrUpdate(String token, AccountId accountId, Platform platform);
}
