package kr.modusplant.shared.event;

import kr.modusplant.domains.member.common.utils.domain.MemberTestUtils;

public interface CommPostUnlikeEventTestUtils extends MemberTestUtils {
    CommPostUnlikeEvent testCommPostUnlikeEvent = CommPostUnlikeEvent.create(testMemberId.getValue(), "01K427DF75VPGA8G78E7BV0EMM");
}
