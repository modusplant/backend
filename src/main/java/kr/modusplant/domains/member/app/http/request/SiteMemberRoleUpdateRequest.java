package kr.modusplant.domains.member.app.http.request;

import kr.modusplant.global.enums.Role;

import java.util.UUID;

public record SiteMemberRoleUpdateRequest(UUID uuid, Role role) {
}
