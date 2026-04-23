package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.notification.domain.vo.CommentPath;
import kr.modusplant.domains.notification.domain.vo.PostId;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;

import java.util.UUID;

public interface CommentInfoRepository {
    UUID getAuthorIdByPostIdAndCommentPath(PostId postId, CommentPath commentPath);

    NotificationPreview getNotificationPreviewByPostIdAndCommentPath(PostId postId, CommentPath commentPath);
}
