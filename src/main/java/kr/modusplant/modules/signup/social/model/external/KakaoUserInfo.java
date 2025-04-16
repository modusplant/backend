package kr.modusplant.modules.signup.social.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoUserInfo {
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

    public String getKakaoId() {
        return this.id.toString();
    }

    public String getKakaoEmail() {
        return this.kakaoAccount.getEmail();
    }

    public String getKakaoNickname() {
        return this.kakaoAccount.getProfile().getNickname();
    }
}
