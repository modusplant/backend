package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import org.jspecify.annotations.NonNull;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;
import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record NeedLinkResult(
        String email,
        String nickname,
        String providerId,
        SocialProvider socialProvider,
        String socialAccessToken
) implements SocialPendingResult {
    @Override
    public @NonNull String toString() {
        return "NeedLinkResult[email=" + maskEmail(email) +
                ", nickname=" + nickname +
                ", providerId=" + maskAnyString(providerId) +
                ", socialProvider=" + socialProvider +
                ", socialAccessToken=" + maskAnyString(socialAccessToken) +
                "]";
    }
}
