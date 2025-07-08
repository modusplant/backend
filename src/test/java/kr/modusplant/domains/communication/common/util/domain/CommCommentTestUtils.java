package kr.modusplant.domains.communication.common.util.domain;

import kr.modusplant.domains.communication.domain.model.CommComment;

import static kr.modusplant.domains.communication.common.util.domain.CommPostTestUtils.TEST_COMM_POST_WITH_ULID;
import static kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils.memberBasicUserWithUuid;

public interface CommCommentTestUtils {
    CommComment TEST_COMM_COMMENT = CommComment.builder()
            .content("테스트 댓글 내용")
            .build();

    CommComment TEST_COMM_COMMENT_WITH_POST_ULID_AND_PATH = CommComment.builder()
            .postUlid(TEST_COMM_POST_WITH_ULID.getUlid())
            .path("1.6.2")
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .content("테스트 댓글 내용")
            .build();
}
