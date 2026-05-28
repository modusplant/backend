package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.comment.domain.event.CommentRegisterEvent;
import kr.modusplant.domains.member.domain.event.CommentLikeEvent;
import kr.modusplant.domains.member.domain.event.PostLikeEvent;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.enums.NotificationActionType;
import kr.modusplant.domains.notification.domain.enums.NotificationStatusType;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.framework.outbound.messaging.FcmSender;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.port.repository.*;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.CursorPageResponse;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final PostInfoRepository postInfoRepository;
    private final CommentInfoRepository commentInfoRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final FcmSender fcmSender;

    private static final int MAX_NOTIFICATION_SIZE = 50;

    /* ======= 알림함 로직 ======= */
    public CursorPageResponse<NotificationResponse> getNotifications(NotificationStatusType status, UUID currentMemberUuid, String lastUlid, int size) {
        List<NotificationReadModel> readModels = notificationQueryRepository.findByStatusWithCursor(status,currentMemberUuid,lastUlid,size);
        boolean hasNext = readModels.size() > size;
        List<NotificationResponse> responses = readModels.stream()
                .limit(size)
                .map(notificationMapper::toNotificationResponse)
                .toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.getLast().ulid() : null;
        return CursorPageResponse.of(responses, nextUlid, hasNext);
    }

    @Transactional
    public void readNotification(String notificationId, UUID currentMemberUuid) {
        notificationRepository.markAsRead(NotificationId.create(notificationId), RecipientId.fromUuid(currentMemberUuid));
    }

    @Transactional
    public void readAllNotifications(UUID currentMemberUuid) {
        notificationRepository.markAllAsRead(RecipientId.fromUuid(currentMemberUuid));
    }

    public Long countUnreadNotifications(UUID currentMemberUuid) {
        return notificationRepository.countByRecipientIdAndStatus(RecipientId.fromUuid(currentMemberUuid), NotificationStatus.unread());
    }

    /* ======= 알림 생성 로직 ======= */
    @Transactional
    public void createPostLikeNotification(PostLikeEvent event) {
        NotificationPreview notificationPreview = postInfoRepository.getNotificationPreviewByPostId(PostId.create(event.getPostUlid()));
        if (notificationPreview.authorUuid() == null || notificationPreview.authorUuid().equals(event.getActorId())) {
            return ;
        }
        Notification notification = notificationMapper.toPostNotification(
                RecipientId.fromUuid(notificationPreview.authorUuid()),
                Actor.fromUuidWithNickname(event.getActorId(), memberInfoRepository.getNicknameByUuid(event.getActorId())),
                NotificationAction.create(NotificationActionType.POST_LIKED),
                PostId.create(event.getPostUlid()),
                ContentPreview.create(notificationPreview.contentPreview())
        );
        Notification postNotification = notificationRepository.saveWithLimit(notification,MAX_NOTIFICATION_SIZE);
        fcmSender.sendAsync(postNotification);
    }

    @Transactional
    public void createCommentLikeNotification(CommentLikeEvent event) {
        NotificationPreview notificationPreview = commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(
                PostId.create(event.getPostUlid()), CommentPath.create(event.getCommentPath())
        );
        if (notificationPreview.authorUuid() == null || notificationPreview.authorUuid().equals(event.getActorId())) {
            return ;
        }
        Notification notification = notificationMapper.toCommentNotification(
                RecipientId.fromUuid(notificationPreview.authorUuid()),
                Actor.fromUuidWithNickname(event.getActorId(), memberInfoRepository.getNicknameByUuid(event.getActorId())),
                NotificationAction.create(NotificationActionType.COMMENT_LIKED),
                PostId.create(event.getPostUlid()),
                CommentPath.create(event.getCommentPath()),
                ContentPreview.create(notificationPreview.contentPreview())
        );
        Notification commentNotification = notificationRepository.saveWithLimit(notification,MAX_NOTIFICATION_SIZE);
        fcmSender.sendAsync(commentNotification);
    }

    @Transactional
    public void createCommentNotification(CommentRegisterEvent event) {
        String nickname = memberInfoRepository.getNicknameByUuid(event.getActorId());
        Actor actor = Actor.fromUuidWithNickname(event.getActorId(), nickname);
        PostId postId = PostId.create(event.getPostUlid());
        CommentPath commentPath = CommentPath.create(event.getCommentPath());
        ContentPreview contentPreview = ContentPreview.create(event.getContentPreview());
        UUID postAuthorUuid = postInfoRepository.getAuthorIdByPostId(postId);

        // 대댓글인 경우 상위 댓글 작성자 알림
        if (NotificationActionType.valueOf(event.getAction()) == NotificationActionType.COMMENT_REPLY_ADDED) {
            CommentPath parentPath = commentPath.extractParentPath();
            UUID parentCommentAuthorUuid = commentInfoRepository.getAuthorIdByPostIdAndCommentPath(postId, parentPath);
            if (parentCommentAuthorUuid != null && !parentCommentAuthorUuid.equals(actor.getId())) {
                Notification commentReplyNotification = notificationRepository.saveWithLimit(
                        notificationMapper.toCommentNotification(
                                RecipientId.fromUuid(parentCommentAuthorUuid), actor,
                                NotificationAction.create(NotificationActionType.COMMENT_REPLY_ADDED),
                                postId, commentPath, contentPreview
                        ),
                        MAX_NOTIFICATION_SIZE
                );
                fcmSender.sendAsync(commentReplyNotification);

                // 게시글 작성자 == 상위 댓글 작성자 인 경우 대댓글 알림 1개만 제공
                if (parentCommentAuthorUuid.equals(postAuthorUuid))
                    return ;
            }
        }

        // 게시글 작성자 알림 생성
        if (postAuthorUuid != null && !postAuthorUuid.equals(actor.getId())) {
            Notification commentNotification = notificationRepository.saveWithLimit(
                    notificationMapper.toCommentNotification(
                            RecipientId.fromUuid(postAuthorUuid), actor,
                            NotificationAction.create(NotificationActionType.COMMENT_ADDED),
                            postId, commentPath, contentPreview
                    ),
                    MAX_NOTIFICATION_SIZE
            );
            fcmSender.sendAsync(commentNotification);
        }
    }

}
