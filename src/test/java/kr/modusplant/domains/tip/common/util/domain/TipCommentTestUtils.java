package kr.modusplant.domains.tip.common.util.domain;

import kr.modusplant.domains.communication.tip.domain.model.TipComment;

import static kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils.memberBasicUserWithUuid;
import static kr.modusplant.domains.tip.common.util.domain.TipPostTestUtils.tipPostWithUlid;

public interface TipCommentTestUtils {

    TipComment tipComment = TipComment.builder()
            .content("테스트 댓글 내용")
            .build();

    TipComment tipCommentWithPostUlidAndPath = TipComment.builder()
            .postUlid(tipPostWithUlid.getUlid())
            .path("/1/6/2")
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .content("테스트 댓글 내용")
            .build();
}
