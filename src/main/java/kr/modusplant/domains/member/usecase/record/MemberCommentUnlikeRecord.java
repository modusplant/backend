package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberCommentUnlikeRecord(UUID memberId, String postUlid, String path) {
}
