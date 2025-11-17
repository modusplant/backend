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
                .primaryCategoryUuid(postEntity.getPrimaryCategory().getUuid())
                .secondaryCategoryUuid(postEntity.getSecondaryCategory().getUuid())
                .authMemberUuid(postEntity.getAuthMember().getUuid())
                .createMemberUuid(postEntity.getCreateMember().getUuid())
                .title(postEntity.getTitle())
                .content(postEntity.getContent())
                .createdAt(postEntity.getCreatedAt())
                .updatedAt(postEntity.getUpdatedAt())
                .publishedAt(postEntity.getPublishedAt())
                .build();
    }
}
