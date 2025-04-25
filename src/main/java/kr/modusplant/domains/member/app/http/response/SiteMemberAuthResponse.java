package kr.modusplant.domains.member.app.http.response;

import kr.modusplant.domains.member.enums.AuthProvider;

import java.util.UUID;

public record SiteMemberAuthResponse(UUID uuid, UUID activeMemberUuid, UUID originalMemberUuid, String email, AuthProvider provider) {
}
