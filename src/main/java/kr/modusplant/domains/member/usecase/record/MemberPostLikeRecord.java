package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberPostLikeRecord(UUID memberId, String postUlid) {
}
