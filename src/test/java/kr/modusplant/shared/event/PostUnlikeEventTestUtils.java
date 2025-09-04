package kr.modusplant.shared.event;

import kr.modusplant.domains.member.common.utils.domain.MemberTestUtils;

public interface PostUnlikeEventTestUtils extends MemberTestUtils {
    PostUnlikeEvent TEST_POST_UNLIKE_EVENT = PostUnlikeEvent.create(testMemberId.getValue(), "01K427DF75VPGA8G78E7BV0EMM");
}
