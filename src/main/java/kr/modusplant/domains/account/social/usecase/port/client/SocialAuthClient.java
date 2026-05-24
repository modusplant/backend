package kr.modusplant.domains.account.social.usecase.port.client;


import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;

public interface SocialAuthClient {
    SocialUserInfo getToken(String code);
    void revokeAccess(String accessToken);
}
