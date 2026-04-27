package kr.modusplant.domains.notification.adapter.mapper;

import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.shared.enums.ContentType;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.modeler.NotificationInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toNotificationResponse(NotificationReadModel readModel) {
        return new NotificationResponse(
                readModel.ulid(),
                readModel.actorId(),
                readModel.actorNickname(),
                readModel.action().name(),
                readModel.status().getValue(),
                readModel.postUlid(),
                readModel.commentPath(),
                resolveContentType(readModel).getValue(),
                readModel.contentPreview(),
                readModel.createdAt()
        );
    }

    @Override
    public Notification toPostNotification(RecipientId recipientId, Actor actor, NotificationAction action, PostId postId, ContentPreview contentPreview) {
        return Notification.createInitial(
                recipientId,
                actor,
                action,
                NotificationStatus.unread(),
                postId,
                null,
                contentPreview
        );
    }

    @Override
    public Notification toCommentNotification(RecipientId recipientId, Actor actor, NotificationAction action, PostId postId, CommentPath commentPath, ContentPreview contentPreview) {
        return Notification.createInitial(
                recipientId,
                actor,
                action,
                NotificationStatus.unread(),
                postId,
                commentPath,
                contentPreview
        );
    }

    private ContentType resolveContentType(NotificationReadModel readModel) {
        return switch (readModel.action()) {
            case POST_LIKED -> ContentType.POST;
            case COMMENT_LIKED, COMMENT_ADDED, COMMENT_REPLY_ADDED -> ContentType.COMMENT;
        };
    }
}
