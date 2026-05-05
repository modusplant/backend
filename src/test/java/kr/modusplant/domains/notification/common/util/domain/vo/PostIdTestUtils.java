package kr.modusplant.domains.notification.common.util.domain.vo;


import kr.modusplant.domains.notification.domain.vo.PostId;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_POST_ULID;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(TEST_NOTIFICATION_POST_ULID);
}
