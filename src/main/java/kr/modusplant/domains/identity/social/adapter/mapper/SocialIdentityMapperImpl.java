package kr.modusplant.domains.identity.social.adapter.mapper;

import kr.modusplant.domains.identity.social.domain.vo.Email;
import kr.modusplant.domains.identity.social.domain.vo.Nickname;
import kr.modusplant.domains.identity.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.identity.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialIdentityMapperImpl implements SocialIdentityMapper {

    @Override
    public SocialUserProfile toSocialUserProfile(AuthProvider provider, SocialUserInfo userInfo) {
        return SocialUserProfile.create(
                SocialCredentials.create(provider,userInfo.getId()),
                Email.create(userInfo.getEmail()),
                Nickname.create(userInfo.getNickname())
        );
    }
}
