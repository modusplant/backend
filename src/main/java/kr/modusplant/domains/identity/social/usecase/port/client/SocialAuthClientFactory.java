package kr.modusplant.domains.identity.social.usecase.port.client;

import kr.modusplant.shared.enums.AuthProvider;

public interface SocialAuthClientFactory {
    SocialAuthClient getClient(AuthProvider provider);
}
