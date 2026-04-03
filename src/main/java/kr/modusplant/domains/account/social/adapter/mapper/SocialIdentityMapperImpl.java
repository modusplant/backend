package kr.modusplant.domains.account.social.adapter.mapper;

import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialIdentityMapperImpl implements SocialIdentityMapper {

    @Override
    public SocialProfile toSocialProfile(SocialProvider provider, SocialUserInfo userInfo) {
        return SocialProfile.create(
                provider,
                userInfo.getId(),
                Email.create(userInfo.getEmail()),
                userInfo.getNickname()
        );
    }

    @Override
    public AuthProvider toSocialAuthProvider(SocialProvider socialProvider) {
        return switch (socialProvider) {
            case KAKAO -> AuthProvider.KAKAO;
            case GOOGLE -> AuthProvider.GOOGLE;
        };
    }

    @Override
    public AuthProvider toLinkedAuthProvider(SocialProvider socialProvider) {
        return switch (socialProvider) {
            case KAKAO -> AuthProvider.BASIC_KAKAO;
            case GOOGLE ->  AuthProvider.BASIC_GOOGLE;
        };
    }

    @Override
    public LoginResult toLoginResult(SocialMemberProfile socialMemberProfile) {
        return new LoginResult(
                socialMemberProfile.getAccountId().getValue(),
                socialMemberProfile.getEmail().getValue(),
                socialMemberProfile.getNickname().getValue(),
                socialMemberProfile.getRole()
        );
    }
}
