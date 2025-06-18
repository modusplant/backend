package kr.modusplant.domains.communication.conversation.common.util.domain;

import kr.modusplant.domains.communication.conversation.domain.model.ConvComment;

import static kr.modusplant.domains.communication.conversation.common.util.domain.ConvPostTestUtils.convPostWithUlid;
import static kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils.memberBasicUserWithUuid;

public interface ConvCommentTestUtils {

    ConvComment convComment = ConvComment.builder()
            .content("테스트 댓글 내용")
            .build();

    ConvComment convCommentWithPostUlidAndPath = ConvComment.builder()
            .postUlid(convPostWithUlid.getUlid())
            .path("1.6.2")
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .content("테스트 댓글 내용")
            .build();
}
