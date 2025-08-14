package kr.modusplant.legacy.modules.auth.social.app.dto;

import kr.modusplant.infrastructure.security.enums.Role;

import java.util.UUID;

public record JwtUserPayload(
        UUID memberUuid,
        String nickname,
        Role role
) { }
