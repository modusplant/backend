package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.RecentlyViewPostRemoveEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID_ARRAY;

public interface RecentlyViewPostRemoveEventTestUtils {
    RecentlyViewPostRemoveEvent testRecentlyViewPostRemoveEvent = RecentlyViewPostRemoveEvent.create(TEST_POST_ULID_ARRAY);
}
