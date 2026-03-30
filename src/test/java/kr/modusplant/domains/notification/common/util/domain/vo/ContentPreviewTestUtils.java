package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.ContentPreview;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_COMMENT_PREVIEW;
import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_POST_PREVIEW;

public interface ContentPreviewTestUtils {
    ContentPreview testPostContentPreview = ContentPreview.create(TEST_NOTIFICATION_POST_PREVIEW);
    ContentPreview testCommentContentPreview = ContentPreview.create(TEST_NOTIFICATION_COMMENT_PREVIEW);
}
