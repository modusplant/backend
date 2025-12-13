package kr.modusplant.domains.identity.account.usecase.response;

import kr.modusplant.shared.enums.AuthProvider;

import java.time.LocalDate;

public record AccountAuthResponse(
        String email,
        AuthProvider authProvider,
        LocalDate createdAt
) {
}
