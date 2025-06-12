package kr.modusplant.domains.member.app.http.request;

import java.util.UUID;

public record SiteMemberAuthUpdateRequest(UUID originalMemberUuid, String email, String pw) {
}
