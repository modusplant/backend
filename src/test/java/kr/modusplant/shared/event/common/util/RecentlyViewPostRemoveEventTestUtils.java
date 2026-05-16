package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.RecentlyViewPostRemoveEvent;

import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID_ARRAY;

public interface RecentlyViewPostRemoveEventTestUtils {
    RecentlyViewPostRemoveEvent testRecentlyViewPostRemoveEvent = RecentlyViewPostRemoveEvent.create(TEST_COMM_POST_ULID_ARRAY);
}
