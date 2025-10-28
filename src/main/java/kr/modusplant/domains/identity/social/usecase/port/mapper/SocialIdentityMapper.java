package kr.modusplant.domains.identity.social.usecase.port.mapper;

import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.shared.enums.AuthProvider;

public interface SocialIdentityMapper {
    SocialUserProfile toSocialUserProfile(AuthProvider provider, SocialUserInfo userInfo);
}
