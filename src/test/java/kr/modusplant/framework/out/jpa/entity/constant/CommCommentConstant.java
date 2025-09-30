package kr.modusplant.framework.out.jpa.entity.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.framework.out.jpa.entity.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommCommentConstant {
    public static final String TEST_COMM_COMMENT_POST_ULID = TEST_COMM_POST_ULID;
    public static final String TEST_COMM_COMMENT_PATH = "1.6.2";
    public static final UUID TEST_COMM_COMMENT_AUTH_MEMBER_UUID = MEMBER_BASIC_USER_UUID;
    public static final UUID TEST_COMM_COMMENT_CREATE_MEMBER_UUID = MEMBER_BASIC_USER_UUID;
    public static final String TEST_COMM_COMMENT_CONTENT = "테스트 댓글 내용";
}
