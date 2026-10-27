package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record MemberCommentChooseRecord(UUID memberId, String postUlid, String path) {
}
