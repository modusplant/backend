package kr.modusplant.modules.auth.social.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.modusplant.modules.auth.social.app.dto.supers.SocialUserInfo;
import lombok.Getter;

@Getter
public class KakaoUserInfo implements SocialUserInfo {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    private static class KakaoAccount {
        private Profile profile;
        private String email;

        @JsonProperty("is_email_verified")
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
