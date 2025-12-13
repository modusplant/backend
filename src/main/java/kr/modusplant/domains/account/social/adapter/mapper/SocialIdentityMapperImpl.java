package kr.modusplant.domains.account.social.adapter.mapper;

import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialIdentityMapperImpl implements SocialIdentityMapper {

    @Override
    public SocialAccountProfile toSocialUserProfile(AuthProvider provider, SocialUserInfo userInfo) {
        return SocialAccountProfile.create(
                SocialCredentials.create(provider,userInfo.getId()),
                Email.create(userInfo.getEmail()),
                Nickname.create(userInfo.getNickname())
        );
    }
}
