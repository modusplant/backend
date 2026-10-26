package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.RecentlyViewMemberRemoveEvent;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface RecentlyViewMemberRemoveEventTestUtils {
    RecentlyViewMemberRemoveEvent testRecentlyViewMemberRemoveEvent = RecentlyViewMemberRemoveEvent.create(MEMBER_BASIC_USER_UUID);
}
