package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.shared.enums.Role;

import java.util.UUID;

public record LoginResult(
        UUID uuid,
        String email,
        String nickname,
        Role role
) implements SocialLoginResult {
}
