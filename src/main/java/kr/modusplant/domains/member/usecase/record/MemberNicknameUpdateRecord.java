package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberNicknameUpdateRecord(UUID id, String nickname) {
}
