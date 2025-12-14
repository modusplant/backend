package kr.modusplant.domains.account.identity.usecase.response;

import kr.modusplant.shared.enums.AuthProvider;

import java.time.LocalDate;

public record IdentityAuthResponse(
        String email,
        AuthProvider authProvider,
        LocalDate createdAt
) {
}
