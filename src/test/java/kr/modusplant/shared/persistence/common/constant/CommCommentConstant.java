package kr.modusplant.shared.persistence.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommCommentConstant {
    public static final String TEST_COMM_COMMENT_POST_ULID = CommPostConstant.TEST_COMM_POST_ULID;
    public static final String TEST_COMM_COMMENT_PATH = "1.6.2";
    public static final UUID TEST_COMM_COMMENT_AUTH_MEMBER_UUID = SiteMemberConstant.MEMBER_BASIC_USER_UUID;
    public static final UUID TEST_COMM_COMMENT_CREATE_MEMBER_UUID = SiteMemberConstant.MEMBER_BASIC_USER_UUID;
    public static final Integer TEST_COMM_COMMENT_LIKE_COUNT = 1;
    public static final String TEST_COMM_COMMENT_CONTENT = "테스트 댓글 내용";
}
