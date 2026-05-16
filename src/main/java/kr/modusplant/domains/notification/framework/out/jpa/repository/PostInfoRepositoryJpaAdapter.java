package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.vo.PostId;
import kr.modusplant.domains.notification.usecase.port.repository.PostInfoRepository;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostInfoRepositoryJpaAdapter implements PostInfoRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public UUID getAuthorIdByPostId(PostId postId) {
        PostEntity postEntity = postJpaRepository.findByUlid(postId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, TableName.COMM_POST));
        MemberEntity memberEntity = postEntity.getAuthMember();
        return  memberEntity != null
                ? memberEntity.getUuid()
                : null;
    }

    @Override
    public NotificationPreview getNotificationPreviewByPostId(PostId postId) {
        PostEntity postEntity = postJpaRepository.findByUlid(postId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, TableName.COMM_POST));
        MemberEntity memberEntity = postEntity.getAuthMember();
        UUID authorUuid = memberEntity != null ? memberEntity.getUuid() : null;
        return new NotificationPreview(authorUuid,postEntity.getTitle());
    }

}
