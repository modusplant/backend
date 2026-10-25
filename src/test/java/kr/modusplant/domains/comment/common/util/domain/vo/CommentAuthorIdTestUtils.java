package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommentAuthorIdTestUtils {
    CommentAuthorId testCommentAuthorId = CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID);
}
