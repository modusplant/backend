package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.shared.enums.Role;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record LoginResult(
        UUID uuid,
        String email,
        String nickname,
        Role role
) implements SocialLoginResult {
    @Override
    public @NonNull String toString() {
        return "LoginResult[uuid=" + uuid +
                ", email=" + maskEmail(email) +
                ", nickname=" + nickname +
                ", role=" + role +
                "]";
    }
}
