package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.framework.out.jpa.entity.NotificationEntity;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationMockRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// TODO: 알림 생성 로직 완성 후 삭제
@Repository
@RequiredArgsConstructor
public class NotificationMockJpaAdapter implements NotificationMockRepository {
    private final NotificationJpaRepository notificationJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public void saveMockNotification(RecipientId recipientId, NotificationAction action, PostId postId, CommentPath commentPath, ContentPreview contentPreview) {
        MemberEntity member = memberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow();
        if (!postJpaRepository.existsByUlid(postId.getValue()) ||
                (commentPath != null && action.getAction() == NotificationActionType.COMMENT_LIKED && !commentJpaRepository.existsByPostUlidAndPath(postId.getValue(), commentPath.getPath()))){
            throw new IllegalArgumentException("Invalid post or comment path");
        }
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .recipient(member)
                .actorId(member.getUuid())
                .actorNickname(member.getNickname())
                .action(action.getAction())
                .status(NotificationStatusType.UNREAD)
                .postUlid(postId.getValue())
                .commentPath(commentPath == null ? null : commentPath.getPath())
                .contentPreview(contentPreview.getContent())
                .build();
        notificationJpaRepository.save(notificationEntity);
    }

    @Override
    public void deleteMockNotification(RecipientId recipientId) {
        notificationJpaRepository.deleteByRecipient(memberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow());
    }
}
