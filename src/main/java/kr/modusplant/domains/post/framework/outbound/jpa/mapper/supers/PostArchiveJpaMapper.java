package kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostArchiveEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;

public interface PostArchiveJpaMapper {
    PostArchiveEntity toPostArchiveEntity(PostEntity postEntity);
}
