package kr.modusplant.domains.member.app.http.request;

import java.time.LocalDate;
import java.util.UUID;

public record SiteMemberUpdateRequest(UUID uuid, String nickname, LocalDate birthDate) {
}
