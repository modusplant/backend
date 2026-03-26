package kr.modusplant.domains.notification.common.util.domain.aggregate;

import kr.modusplant.domains.notification.common.util.domain.vo.*;
import kr.modusplant.domains.notification.domain.aggregate.Notification;

public interface NotificationTestUtils extends NotificationIdTestUtils, RecipientIdTestUtils, ActorIdTestUtils, NotificationActionTestUtils, NotificationStatusTestUtils, PostIdTestUtils, CommentPathTestUtils, ContentPreviewTestUtils {

    default Notification createPostLikedReadNotification() {
        return Notification.create(testNotificationId, RecipientIdTestUtils.testRecipientId, ActorIdTestUtils.testActorId, NotificationActionTestUtils.testNotificationActionPostLiked, testNotificationStatusRead, PostIdTestUtils.testPostId, null, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testPostContentPreview);
    }

    default Notification createPostLikedUnreadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionPostLiked, testNotificationStatusUnread, testPostId, null, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testPostContentPreview);
    }

    default Notification createCommentLikedReadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionCommentLiked, testNotificationStatusRead, testPostId, testCommentPath, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testCommentContentPreview);
    }

    default Notification createCommentLikedUnreadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionCommentLiked, testNotificationStatusUnread, testPostId, testCommentPath, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testCommentContentPreview);
    }

    default Notification createCommentAddedReadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionCommentAdded, testNotificationStatusRead, testPostId, testCommentPath, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testCommentContentPreview);
    }

    default Notification createCommentAddedUnreadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionCommentAdded, testNotificationStatusUnread, testPostId, testCommentPath, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testCommentContentPreview);
    }

    default Notification createCommentReplyAddedReadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionCommentReplyAdded, testNotificationStatusRead, testPostId, testCommentPath, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testCommentContentPreview);
    }

    default Notification createCommentReplyAddedUnreadNotification() {
        return Notification.create(testNotificationId,testRecipientId, testActorId, testNotificationActionCommentReplyAdded, testNotificationStatusUnread, testPostId, testCommentPath, kr.modusplant.domains.notification.common.util.domain.vo.ContentPreviewTestUtils.testCommentContentPreview);
    }

}
