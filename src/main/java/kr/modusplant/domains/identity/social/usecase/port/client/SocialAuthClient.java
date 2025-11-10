package kr.modusplant.domains.identity.social.usecase.port.client;

import kr.modusplant.domains.identity.social.usecase.port.client.dto.SocialUserInfo;

public interface SocialAuthClient {
    String getAccessToken(String code);
    SocialUserInfo getUserInfo(String accessToken);
}
