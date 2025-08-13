package kr.modusplant.legacy.domains.member.app.http.response;

import kr.modusplant.legacy.domains.member.enums.AuthProvider;

import java.util.UUID;

public record SiteMemberAuthResponse(UUID originalMemberUuid, UUID activeMemberUuid, String email, AuthProvider provider) {
}
