package kr.modusplant.legacy.modules.auth.social.app.service.supers;

import kr.modusplant.legacy.modules.auth.social.app.dto.supers.SocialUserInfo;

public interface SocialAuthClient {
    String getAccessToken(String code);
    SocialUserInfo getUserInfo(String accessToken);
}
