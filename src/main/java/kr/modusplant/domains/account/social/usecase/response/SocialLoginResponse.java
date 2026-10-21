package kr.modusplant.domains.account.social.usecase.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.modusplant.domains.account.social.usecase.enums.OAuthType;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;
import static kr.modusplant.shared.util.MaskUtils.maskEmail;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SocialLoginResponse(
        OAuthType type,

        String accessToken,

        String email,

        String nickname
) {
    public static SocialLoginResponse login(String accessToken) {
        return new SocialLoginResponse(OAuthType.LOGIN, accessToken,null,null);
    }

    public static SocialLoginResponse needSignup(String email,String nickname) {
        return new SocialLoginResponse(OAuthType.NEED_SIGNUP, null, email, nickname);
    }

    public static SocialLoginResponse needLink(String email, String nickname) {
        return new SocialLoginResponse(OAuthType.NEED_LINK, null, email, nickname);
    }

    @Override
    public String toString() {
        return "SocialLoginResponse[type=" + type +
                ", accessToken=" + maskAnyString(accessToken) +
                ", email=" + maskEmail(email) +
                ", nickname=" + nickname +
                "]";
    }
}
