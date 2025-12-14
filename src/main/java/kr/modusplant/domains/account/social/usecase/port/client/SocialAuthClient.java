package kr.modusplant.domains.account.social.usecase.port.client;

import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;

public interface SocialAuthClient {
    String getAccessToken(String code);
    SocialUserInfo getUserInfo(String accessToken);
}
