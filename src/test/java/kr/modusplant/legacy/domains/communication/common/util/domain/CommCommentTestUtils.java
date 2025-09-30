package kr.modusplant.legacy.domains.communication.common.util.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils.TEST_COMM_POST_ULID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommCommentTestUtils {
    String TEST_COMM_COMMENT_POST_ULID = TEST_COMM_POST_ULID;
    String TEST_COMM_COMMENT_PATH = "1.6.2";
    UUID TEST_COMM_COMMENT_AUTH_MEMBER_UUID = MEMBER_BASIC_USER_UUID;
    UUID TEST_COMM_COMMENT_CREATE_MEMBER_UUID = MEMBER_BASIC_USER_UUID;
    String TEST_COMM_COMMENT_CONTENT = "테스트 댓글 내용";
}
