package kr.modusplant.shared.event;

import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;

public interface CommPostLikeEventTestUtils extends MemberTestUtils {
    CommPostLikeEvent testCommPostLikeEvent = CommPostLikeEvent.create(testMemberId.getValue(), "01K427DF75VPGA8G78E7BV0EMM");
}
