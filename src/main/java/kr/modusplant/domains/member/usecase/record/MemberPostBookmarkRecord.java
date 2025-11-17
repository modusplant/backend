package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberPostBookmarkRecord(UUID memberId, String postUlid) {
}
