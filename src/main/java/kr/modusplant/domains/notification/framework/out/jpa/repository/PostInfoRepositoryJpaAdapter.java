package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.vo.PostId;
import kr.modusplant.domains.notification.usecase.port.repository.PostInfoRepository;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostInfoRepositoryJpaAdapter implements PostInfoRepository {
    private final CommPostJpaRepository postJpaRepository;

    @Override
    public UUID getAuthorIdByPostId(PostId postId) {
        CommPostEntity postEntity = postJpaRepository.findByUlid(postId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, TableName.COMM_POST));
        SiteMemberEntity memberEntity = postEntity.getAuthMember();
        return  memberEntity != null
                ? memberEntity.getUuid()
                : null;
    }

    @Override
    public NotificationPreview getNotificationPreviewByPostId(PostId postId) {
        CommPostEntity postEntity = postJpaRepository.findByUlid(postId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, TableName.COMM_POST));
        SiteMemberEntity memberEntity = postEntity.getAuthMember();
        UUID authorUuid = memberEntity != null ? memberEntity.getUuid() : null;
        return new NotificationPreview(authorUuid,postEntity.getTitle());
    }

}
