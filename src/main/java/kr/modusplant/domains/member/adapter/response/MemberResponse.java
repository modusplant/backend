package kr.modusplant.domains.member.adapter.response;

import java.time.LocalDate;
import java.util.UUID;

public record MemberResponse(UUID uuid, String status, String nickname, LocalDate birthDate) {
}
