package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostArchiveJpaMapper;
import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import org.springframework.stereotype.Component;

@Component
public class PostArchiveJpaMapperImpl implements PostArchiveJpaMapper {
    @Override
    public CommPostArchiveEntity toPostArchiveEntity(CommPostEntity postEntity) {
        return CommPostArchiveEntity.builder()
                .ulid(postEntity.getUlid())
                .primaryCategoryId(postEntity.getPrimaryCategory().getId())
                .secondaryCategoryId(postEntity.getSecondaryCategory().getId())
                .authMemberUuid(postEntity.getAuthMember().getUuid())
                .title(postEntity.getTitle())
                .content(postEntity.getContent())
                .createdAt(postEntity.getCreatedAt())
                .updatedAt(postEntity.getUpdatedAt())
                .publishedAt(postEntity.getPublishedAt())
                .build();
    }
}
