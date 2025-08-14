package kr.modusplant.legacy.domains.member.app.http.response;

import kr.modusplant.infrastructure.security.enums.Role;

import java.util.UUID;

public record SiteMemberRoleResponse(UUID uuid, Role role) {
}
