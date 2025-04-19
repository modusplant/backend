package kr.modusplant.modules.auth.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.modusplant.modules.auth.social.dto.supers.SocialUserInfo;
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
        return this.id.toString();
    }

    @Override
    public String getEmail() {
        return this.kakaoAccount.getEmail();
    }

    @Override
    public String getNickname() {
        return this.kakaoAccount.getProfile().getNickname();
    }
}
