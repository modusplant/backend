package kr.modusplant.domains.account.social.framework.outbound.client.dto;

import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record IdTokenInfo(
        String id,
        String email,
        String nickname
) {
    @Override
    public String toString() {
        return "IdTokenInfo[id=" + id +
                ", email=" + maskEmail(email) +
                ", nickname=" + nickname +
                "]";
    }
}
