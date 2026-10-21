package kr.modusplant.domains.account.identity.usecase.response;

import kr.modusplant.shared.enums.AuthProvider;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

import static kr.modusplant.shared.util.MaskUtils.maskEmail;

public record IdentityAuthResponse(
        String email,
        AuthProvider authProvider,
        LocalDate createdAt
) {
    @Override
    public @NonNull String toString() {
        return "IdentityAuthResponse[email=" + maskEmail(email) +
                ", authProvider=" + authProvider +
                ", createdAt=" + createdAt +
                "]";
    }
}
