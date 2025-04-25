package kr.modusplant.domains.member.app.http.request;

import java.util.UUID;

public record SiteMemberAuthUpdateRequest(UUID activeMemberUuid, UUID originalMemberUuid, String email, String pw) {
}
