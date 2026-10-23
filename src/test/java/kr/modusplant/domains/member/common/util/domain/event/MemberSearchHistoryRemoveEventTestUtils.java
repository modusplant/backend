package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.MemberSearchHistoryRemoveEvent;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberSearchHistoryRemoveEventTestUtils {
    MemberSearchHistoryRemoveEvent testMemberSearchHistoryRemoveEvent = MemberSearchHistoryRemoveEvent.create(MEMBER_BASIC_USER_UUID);
}
