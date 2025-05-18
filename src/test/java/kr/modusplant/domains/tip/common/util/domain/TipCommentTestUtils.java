package kr.modusplant.domains.tip.common.util.domain;

import kr.modusplant.domains.tip.domain.model.TipComment;

import java.util.UUID;

import static kr.modusplant.domains.tip.common.util.domain.TipPostTestUtils.tipPost;
import static kr.modusplant.domains.tip.common.util.domain.TipPostTestUtils.tipPostWithUlid;

public interface TipCommentTestUtils {

    TipComment convComment = TipComment.builder()
            .postUlid(tipPostWithUlid.getUlid())
            .materializedPath("/1/6/2")
            .authMemberUuid(UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd"))
            .createMemberUuid(UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"))
            .content("테스트 댓글 내용")
            .build();
}
