package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;

import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record TempTokenInfo(
        String email,
        String providerId,
        SocialProvider socialProvider
) {
    @Override
    public String toString() {
        return "TempTokenInfo[email=" + maskEmail(email) +
                ", providerId=" + providerId +
                ", socialProvider=" + socialProvider +
                "]";
    }
}
