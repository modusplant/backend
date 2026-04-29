package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.notification.domain.vo.PostId;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;

import java.util.UUID;

public interface PostInfoRepository {

    UUID getAuthorIdByPostId(PostId postId);

    NotificationPreview getNotificationPreviewByPostId(PostId postId);
}
