package kr.modusplant.domains.account.social.usecase.port.mapper;

import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.shared.enums.AuthProvider;

public interface SocialIdentityMapper {
    SocialAccountProfile toSocialUserProfile(AuthProvider provider, SocialUserInfo userInfo);
}
