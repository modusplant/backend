package kr.modusplant.domains.member.app.http.response;

import kr.modusplant.domains.member.enums.AuthProvider;

import java.util.UUID;

public record SiteMemberAuthResponse(UUID originalMemberUuid, UUID activeMemberUuid, String email, AuthProvider provider) {
}
