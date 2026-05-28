package kr.modusplant.domains.post.framework.outbound.jpa.mapper;

import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostArchiveEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers.PostArchiveJpaMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostArchiveJpaMapperImpl implements PostArchiveJpaMapper {
    @Override
    public PostArchiveEntity toPostArchiveEntity(PostEntity postEntity) {
        return PostArchiveEntity.builder()
                .ulid(postEntity.getUlid())
                .primaryCategoryId(postEntity.getPrimaryCategory().getId())
                .secondaryCategoryId(postEntity.getSecondaryCategory().getId())
                .authMemberUuid(postEntity.getAuthMember() != null ? postEntity.getAuthMember().getUuid() : null)
                .title(postEntity.getTitle())
                .contentText(postEntity.getContentText())
                .createdAt(postEntity.getCreatedAt())
                .archivedAt(LocalDateTime.now())
                .updatedAt(postEntity.getUpdatedAt())
                .publishedAt(postEntity.getPublishedAt())
                .build();
    }
}
