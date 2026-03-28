package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationMockRepository;
import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// TODO: 알림 생성 로직 완성 후 삭제
@Repository
@RequiredArgsConstructor
public class NotificationMockJpaAdapter implements NotificationMockRepository {
    private final NotificationJpaRepository notificationJpaRepository;
    private final SiteMemberJpaRepository siteMemberJpaRepository;
    private final CommPostJpaRepository commPostJpaRepository;
    private final CommCommentJpaRepository commCommentJpaRepository;

    @Override
    public void saveMockNotification(RecipientId recipientId, NotificationAction action, PostId postId, CommentPath commentPath, ContentPreview contentPreview) {
        SiteMemberEntity member = siteMemberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow();
        if (!commPostJpaRepository.existsByUlid(postId.getValue()) ||
                (commentPath != null && action.getAction() == NotificationActionType.COMMENT_LIKED && !commCommentJpaRepository.existsByPostUlidAndPath(postId.getValue(), commentPath.getPath()))){
            throw new IllegalArgumentException("Invalid post or comment path");
        }
        CommNotificationEntity commNotificationEntity = CommNotificationEntity.builder()
                .recipient(member)
                .actorId(member.getUuid())
                .actorNickname(member.getNickname())
                .action(action.getAction())
                .status(NotificationStatusType.UNREAD)
                .postUlid(postId.getValue())
                .commentPath(commentPath == null ? null : commentPath.getPath())
                .contentPreview(contentPreview.getContent())
                .build();
        notificationJpaRepository.save(commNotificationEntity);
    }

    @Override
    public void deleteMockNotification(RecipientId recipientId) {
        notificationJpaRepository.deleteByRecipient(siteMemberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow());
    }
}
