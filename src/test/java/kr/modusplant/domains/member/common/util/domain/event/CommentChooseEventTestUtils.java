package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.CommentChooseEvent;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentChooseEventTestUtils {
    CommentChooseEvent testCommentChooseEvent = CommentChooseEvent.create(MEMBER_BASIC_USER_UUID, TEST_POST_ULID, TEST_COMMENT_PATH);
}
