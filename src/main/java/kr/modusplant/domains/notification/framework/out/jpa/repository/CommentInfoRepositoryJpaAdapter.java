package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.domain.vo.CommentPath;
import kr.modusplant.domains.notification.domain.vo.PostId;
import kr.modusplant.domains.notification.usecase.port.repository.CommentInfoRepository;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentInfoRepositoryJpaAdapter implements CommentInfoRepository {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public UUID getAuthorIdByPostIdAndCommentPath(PostId postId, CommentPath commentPath) {
        CommentEntity commentEntity = commentJpaRepository.findByPostUlidAndPath(postId.getValue(), commentPath.getPath())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT));
        MemberEntity memberEntity = commentEntity.getAuthMember();
        return memberEntity != null
                ? memberEntity.getUuid()
                : null;
    }

    @Override
    public NotificationPreview getNotificationPreviewByPostIdAndCommentPath(PostId postId, CommentPath commentPath) {
        CommentEntity commentEntity = commentJpaRepository.findByPostUlidAndPath(postId.getValue(), commentPath.getPath())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT));
        MemberEntity memberEntity = commentEntity.getAuthMember();
        UUID authorUuid = memberEntity != null ? memberEntity.getUuid() : null;
        return new NotificationPreview(authorUuid, commentEntity.getContent());
    }
}
