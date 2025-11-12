package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberCancelPostBookmarkRecord(UUID memberId, String postUlid) {
}
