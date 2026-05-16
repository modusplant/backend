package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.framework.jpa.entity.PostArchiveEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;

public interface PostArchiveJpaMapper {
    PostArchiveEntity toPostArchiveEntity(PostEntity postEntity);
}
