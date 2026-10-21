package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;
import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record NeedSignupResult(
        String email,
        String nickname,
        String providerId,
        SocialProvider socialProvider,
        String socialAccessToken
) implements SocialPendingResult {
    @Override
    public String toString() {
        return "NeedSignupResult[email=" + maskEmail(email) +
                ", nickname=" + nickname +
                ", providerId=" + providerId +
                ", socialProvider=" + socialProvider +
                ", socialAccessToken=" + maskAnyString(socialAccessToken) +
                "]";
    }
}
