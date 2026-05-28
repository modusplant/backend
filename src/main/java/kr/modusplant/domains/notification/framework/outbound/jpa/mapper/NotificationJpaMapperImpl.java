package kr.modusplant.domains.notification.framework.outbound.jpa.mapper;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.NotificationEntity;
import kr.modusplant.domains.notification.framework.outbound.jpa.mapper.supers.NotificationJpaMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationJpaMapperImpl implements NotificationJpaMapper {
    @Override
    public NotificationEntity toNotificationEntity(Notification notification, MemberEntity recipient) {
        return NotificationEntity.builder()
                .recipient(recipient)
                .actorId(notification.getActor().getId())
                .actorNickname(notification.getActor().getNickname())
                .action(notification.getAction().getAction())
                .status(notification.getStatus().getStatus())
                .postUlid(notification.getPostId().getValue())
                .commentPath(notification.getCommentPath()!=null ? notification.getCommentPath().getPath() : null)
                .contentPreview(notification.getContentPreview().getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Override
    public Notification toNotification(NotificationEntity notificationEntity) {
        CommentPath commentPath = notificationEntity.getCommentPath() != null
                ? CommentPath.create(notificationEntity.getCommentPath())
                : null;
        return Notification.create(
                NotificationId.create(notificationEntity.getUlid()),
                RecipientId.fromUuid(notificationEntity.getRecipient().getUuid()),
                Actor.fromUuidWithNickname(notificationEntity.getActorId(),notificationEntity.getActorNickname()),
                NotificationAction.create(notificationEntity.getAction()),
                NotificationStatus.create(notificationEntity.getStatus()),
                PostId.create(notificationEntity.getPostUlid()),
                commentPath,
                ContentPreview.create(notificationEntity.getContentPreview()),
                notificationEntity.getCreatedAt()
        );
    }
}
