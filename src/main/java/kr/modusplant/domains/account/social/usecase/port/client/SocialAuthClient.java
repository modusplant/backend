package kr.modusplant.domains.account.social.usecase.port.client;


import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;

public interface SocialAuthClient {
    SocialProvider getProvider();
    SocialUserInfo getTokenInfo(String code, boolean isLocal);
    void revokeAccess(String accessToken);
}
