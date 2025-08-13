package kr.modusplant.legacy.domains.communication.common.util.domain;

import kr.modusplant.legacy.domains.communication.domain.model.CommCommentLike;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberTestUtils;

public interface CommLikeTestUtils extends CommPostTestUtils, SiteMemberTestUtils {
    CommCommentLike TEST_COMMENT_LIKE = CommCommentLike.builder()
            .postId(TEST_COMM_POST_WITH_ULID.getUlid())
            .memberId(memberBasicUserWithUuid.getUuid())
            .build();
}
