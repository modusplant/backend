package kr.modusplant.modules.auth.social.service.supers;

import kr.modusplant.modules.auth.social.dto.supers.SocialUserInfo;

public interface SocialAuthClient {
    String getAccessToken(String code);
    SocialUserInfo getUserInfo(String accessToken);
}
