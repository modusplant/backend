package kr.modusplant.legacy.domains.communication.common.util.domain;

import kr.modusplant.legacy.domains.communication.domain.model.CommComment;

import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils.TEST_COMM_POST_WITH_ULID;

public interface CommCommentTestUtils {
    CommComment TEST_COMM_COMMENT = CommComment.builder()
            .content("테스트 댓글 내용")
            .build();

    CommComment TEST_COMM_COMMENT_WITH_POST_ULID_AND_PATH = CommComment.builder()
            .postUlid(TEST_COMM_POST_WITH_ULID.getUlid())
            .path("1.6.2")
            .authMemberUuid(MEMBER_BASIC_USER_UUID)
            .createMemberUuid(MEMBER_BASIC_USER_UUID)
            .content("테스트 댓글 내용")
            .build();
}
