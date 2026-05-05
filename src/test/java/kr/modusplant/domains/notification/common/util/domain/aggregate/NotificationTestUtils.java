package kr.modusplant.domains.notification.common.util.domain.aggregate;

import kr.modusplant.domains.notification.common.util.domain.vo.*;
import kr.modusplant.domains.notification.domain.aggregate.Notification;

import java.time.LocalDateTime;

public interface NotificationTestUtils extends NotificationIdTestUtils, RecipientIdTestUtils, ActorTestUtils, NotificationActionTestUtils, NotificationStatusTestUtils, PostIdTestUtils, CommentPathTestUtils, ContentPreviewTestUtils {

    default Notification createPostLikedReadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId, testRecipientId, testActor, testNotificationActionPostLiked, testNotificationStatusRead, testPostId, null, testPostContentPreview, createdAt);
    }

    default Notification createPostLikedUnreadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionPostLiked, testNotificationStatusUnread, testPostId, null, testPostContentPreview, createdAt);
    }

    default Notification createCommentLikedReadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionCommentLiked, testNotificationStatusRead, testPostId, testCommentPath, testCommentContentPreview, createdAt);
    }

    default Notification createCommentLikedUnreadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionCommentLiked, testNotificationStatusUnread, testPostId, testCommentPath, testCommentContentPreview, createdAt);
    }

    default Notification createCommentAddedReadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionCommentAdded, testNotificationStatusRead, testPostId, testCommentPath, testCommentContentPreview, createdAt);
    }

    default Notification createCommentAddedUnreadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionCommentAdded, testNotificationStatusUnread, testPostId, testCommentPath, testCommentContentPreview, createdAt);
    }

    default Notification createCommentReplyAddedReadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionCommentReplyAdded, testNotificationStatusRead, testPostId, testCommentPath, testCommentContentPreview, createdAt);
    }

    default Notification createCommentReplyAddedUnreadNotification(LocalDateTime createdAt) {
        return Notification.create(testNotificationId,testRecipientId, testActor, testNotificationActionCommentReplyAdded, testNotificationStatusUnread, testPostId, testCommentPath, testCommentContentPreview, createdAt);
    }

}
