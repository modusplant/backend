package kr.modusplant.domains.account.social.framework.outbound.client.dto;

import org.jspecify.annotations.NonNull;

import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record IdTokenInfo(
        String id,
        String email,
        String nickname
) {
    @Override
    public @NonNull String toString() {
        return "IdTokenInfo[id=" + id +
                ", email=" + maskEmail(email) +
                ", nickname=" + nickname +
                "]";
    }
}
