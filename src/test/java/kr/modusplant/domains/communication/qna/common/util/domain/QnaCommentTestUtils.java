package kr.modusplant.domains.communication.qna.common.util.domain;

import kr.modusplant.domains.communication.qna.domain.model.QnaComment;

import static kr.modusplant.domains.communication.qna.common.util.domain.QnaPostTestUtils.testQnaPostWithUlid;
import static kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils.memberBasicUserWithUuid;

public interface QnaCommentTestUtils {
    QnaComment testQnaComment = QnaComment.builder()
            .content("테스트 댓글 내용")
            .build();

    QnaComment testQnaCommentWithPostUlidAndPath = QnaComment.builder()
            .postUlid(testQnaPostWithUlid.getUlid())
            .path("1.6.2")
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .content("테스트 댓글 내용")
            .build();
}
