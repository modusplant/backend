package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.entity.CommentAuthor;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;


public interface AuthorTestUtils {

    CommentAuthor testCommentAuthor = CommentAuthor.create(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_USER_NICKNAME);
    CommentAuthor testCommentAuthorWithUuid = CommentAuthor.create(MEMBER_BASIC_USER_UUID);

}
