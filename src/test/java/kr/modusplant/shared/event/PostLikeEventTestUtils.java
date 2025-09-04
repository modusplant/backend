package kr.modusplant.shared.event;

import kr.modusplant.domains.member.common.utils.domain.aggregate.MemberTestUtils;

public interface PostLikeEventTestUtils extends MemberTestUtils {
    PostLikeEvent TEST_POST_LIKE_EVENT = PostLikeEvent.create(testMemberId.getValue(), "01K427DF75VPGA8G78E7BV0EMM");
}
