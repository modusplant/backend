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
                Nickname.create(normalizeNickname(userInfo.getNickname()))
        );
    }

    // TODO: 소셜로그인 구현 완료 후 삭제 (테스트용 임시 로직)
    private String normalizeNickname(String socialNickname) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (socialNickname == null) return null;
        String nickname = socialNickname.replaceAll("\\s+", "");
        nickname = nickname.replaceAll("[^가-힣A-Za-z0-9]", "");
        if (nickname.length() > 12)
            nickname = nickname.substring(0,12);
        for (int i = 0; i < 4; i++) {                       // 4글자 랜덤
            nickname += chars.charAt((int)(Math.random() * chars.length()));
        }
        return nickname;
    }
}
