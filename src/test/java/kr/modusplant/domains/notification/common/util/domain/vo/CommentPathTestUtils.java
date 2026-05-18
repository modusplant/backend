package kr.modusplant.domains.notification.common.util.domain.vo;


import kr.modusplant.domains.notification.domain.vo.CommentPath;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_COMMENT_PATH_DEPTH3;

public interface CommentPathTestUtils {
    CommentPath testCommentPath = CommentPath.create(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3);
}
