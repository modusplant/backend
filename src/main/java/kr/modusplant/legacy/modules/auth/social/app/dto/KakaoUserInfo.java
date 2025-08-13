package kr.modusplant.legacy.modules.auth.social.app.dto;

import kr.modusplant.legacy.modules.auth.social.app.dto.supers.SocialUserInfo;
import lombok.Getter;

@Getter
public class KakaoUserInfo implements SocialUserInfo {
    private Long id;

    private KakaoAccount kakaoAccount;

    @Getter
    private static class KakaoAccount {
        private Profile profile;
        private String email;

        private Boolean isEmailVerified;
    }

    @Getter
    private static class Profile {
        private String nickname;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getNickname() {
        return kakaoAccount.getProfile().getNickname();
    }
}
