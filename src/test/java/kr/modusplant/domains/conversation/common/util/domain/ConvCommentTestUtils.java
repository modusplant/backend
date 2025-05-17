package kr.modusplant.domains.conversation.common.util.domain;

import kr.modusplant.domains.conversation.domain.model.ConvComment;
import kr.modusplant.domains.term.domain.model.Term;

import java.util.UUID;

import static kr.modusplant.domains.conversation.common.util.domain.ConvPostTestUtils.convPost;
import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface ConvCommentTestUtils {

    ConvComment convComment = ConvComment.builder()
            .postUlid(convPost.getUlid())
            .materializedPath("/1/6/2")
            .authMemberUuid(UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd"))
            .createMemberUuid(UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"))
            .content("테스트 댓글 내용")
            .build();
}
