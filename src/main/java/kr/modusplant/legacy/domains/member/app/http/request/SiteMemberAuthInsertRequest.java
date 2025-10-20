package kr.modusplant.legacy.domains.member.app.http.request;

import kr.modusplant.legacy.domains.member.enums.AuthProvider;

import java.util.UUID;

public record SiteMemberAuthInsertRequest(UUID originalMemberUuid, String email, String pw, AuthProvider provider, String providerId) {
}
