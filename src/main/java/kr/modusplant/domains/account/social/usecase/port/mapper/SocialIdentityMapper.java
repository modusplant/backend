package kr.modusplant.domains.account.social.usecase.port.mapper;

import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.shared.enums.AuthProvider;

public interface SocialIdentityMapper {
    SocialProfile toSocialProfile(SocialProvider provider, SocialUserInfo userInfo);

    AuthProvider toSocialAuthProvider(SocialProvider socialProvider);

    AuthProvider toLinkedAuthProvider(SocialProvider socialProvider);

    LoginResult toLoginResult(SocialMemberProfile socialMemberProfile);
}
