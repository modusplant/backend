package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostArchiveJpaMapper;
import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostArchiveJpaMapperImpl implements PostArchiveJpaMapper {
    @Override
    public CommPostArchiveEntity toPostArchiveEntity(CommPostEntity postEntity) {
        return CommPostArchiveEntity.builder()
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
