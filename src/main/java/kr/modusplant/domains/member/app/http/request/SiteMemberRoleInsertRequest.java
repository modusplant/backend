package kr.modusplant.domains.member.app.http.request;

import kr.modusplant.global.enums.Role;

import java.util.UUID;

public record SiteMemberRoleInsertRequest(UUID uuid, Role role) {
}
