package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberPostUnlikeRecord(UUID memberId, String postUlid) {
}
