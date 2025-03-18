package kr.modusplant.api.crud.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoUserInfo {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    public static class KakaoAccount {
        private Profile profile;
        private String email;
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
    }

    @Getter
    public static class Profile {
        private String nickname;
    }
}
