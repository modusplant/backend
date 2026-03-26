package kr.modusplant.domains.notification.common.util.domain.vo;


import kr.modusplant.domains.notification.domain.vo.CommentPath;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_COMMENT_PATH;

public interface CommentPathTestUtils {
    CommentPath testCommentPath = CommentPath.create(TEST_NOTIFICATION_COMMENT_PATH);
}
