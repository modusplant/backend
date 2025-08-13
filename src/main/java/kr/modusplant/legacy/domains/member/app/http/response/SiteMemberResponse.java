package kr.modusplant.legacy.domains.member.app.http.response;

import java.time.LocalDate;
import java.util.UUID;

public record SiteMemberResponse(UUID uuid, String nickname, LocalDate birthDate, Boolean isActive) {
}
