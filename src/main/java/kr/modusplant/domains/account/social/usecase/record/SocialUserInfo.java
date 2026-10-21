package kr.modusplant.domains.account.social.usecase.record;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;
import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record SocialUserInfo(
        String socialAccessToken,

        String id,

        String email,

        String nickname
) {
    @Override
    public String toString() {
        return "SocialUserInfo[socialAccessToken=" + maskAnyString(socialAccessToken) +
                ", id=" + id +
                ", email=" + maskEmail(email) +
                ", nickname=" + nickname +
                "]";
    }
}
