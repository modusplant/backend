package kr.modusplant.domains.identity.account.usecase.response;

import kr.modusplant.shared.enums.AuthProvider;

public record AccountAuthResponse(
        String email,
        AuthProvider authProvider
) {
}
