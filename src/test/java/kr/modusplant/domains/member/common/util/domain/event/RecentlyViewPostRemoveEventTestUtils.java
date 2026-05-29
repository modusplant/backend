package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.RecentlyViewPostRemoveEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID_ARRAY;

public interface RecentlyViewPostRemoveEventTestUtils {
    RecentlyViewPostRemoveEvent testRecentlyViewPostRemoveEvent = RecentlyViewPostRemoveEvent.create(TEST_POST_ULID_ARRAY);
}
