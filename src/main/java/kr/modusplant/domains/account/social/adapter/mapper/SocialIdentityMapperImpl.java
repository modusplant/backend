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
