package kr.modusplant.shared.event.common.util;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.shared.event.PostLikeEvent;

public interface PostLikeEventTestUtils extends MemberTestUtils {
    PostLikeEvent TEST_POST_LIKE_EVENT = PostLikeEvent.create(testMemberId.getValue(), "01K427DF75VPGA8G78E7BV0EMM");
}
