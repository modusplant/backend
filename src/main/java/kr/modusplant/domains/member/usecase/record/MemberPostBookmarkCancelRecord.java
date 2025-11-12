package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberPostBookmarkCancelRecord(UUID memberId, String postUlid) {
}
