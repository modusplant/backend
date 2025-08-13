package kr.modusplant.legacy.domains.member.app.http.request;

import java.util.UUID;

public record SiteMemberAuthUpdateRequest(UUID originalMemberUuid, String email, String pw) {
}
