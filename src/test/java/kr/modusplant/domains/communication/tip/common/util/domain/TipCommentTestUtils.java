package kr.modusplant.domains.communication.tip.common.util.domain;

import kr.modusplant.domains.communication.tip.domain.model.TipComment;

import static kr.modusplant.domains.communication.tip.common.util.domain.TipPostTestUtils.testTipPostWithUlid;
import static kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils.memberBasicUserWithUuid;

public interface TipCommentTestUtils {

    TipComment tipComment = TipComment.builder()
            .content("테스트 댓글 내용")
            .build();

    TipComment tipCommentWithPostUlidAndPath = TipComment.builder()
            .postUlid(testTipPostWithUlid.getUlid())
            .path("1.6.2")
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .content("테스트 댓글 내용")
            .build();
}
