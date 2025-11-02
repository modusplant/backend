package kr.modusplant.legacy.domains.member.app.http.request;

import kr.modusplant.infrastructure.security.enums.Role;

import java.util.UUID;

public record SiteMemberRoleInsertRequest(UUID uuid, Role role) {
}
