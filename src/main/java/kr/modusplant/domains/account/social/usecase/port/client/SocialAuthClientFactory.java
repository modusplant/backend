package kr.modusplant.domains.account.social.usecase.port.client;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;

public interface SocialAuthClientFactory {
    SocialAuthClient getClient(SocialProvider provider);
}
