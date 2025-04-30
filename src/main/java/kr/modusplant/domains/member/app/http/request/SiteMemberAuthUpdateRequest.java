package kr.modusplant.domains.member.app.http.request;

import java.util.UUID;

public record SiteMemberAuthUpdateRequest(UUID originalMemberUuid, UUID activeMemberUuid, String email, String pw) {
}
