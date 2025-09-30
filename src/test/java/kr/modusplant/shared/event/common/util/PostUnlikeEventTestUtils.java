package kr.modusplant.shared.event.common.util;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.shared.event.PostUnlikeEvent;

public interface PostUnlikeEventTestUtils extends MemberTestUtils {
    PostUnlikeEvent TEST_POST_UNLIKE_EVENT = PostUnlikeEvent.create(testMemberId.getValue(), "01K427DF75VPGA8G78E7BV0EMM");
}
