package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.vo.CommentContent;

public interface CommentContentTestUtils {
    CommentContent testCommentContent = CommentContent.create("테스트용 댓글 내용입니다.");
}
