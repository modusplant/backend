package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.PostArchiveEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;

public interface PostArchiveJpaMapper {
    PostArchiveEntity toPostArchiveEntity(PostEntity postEntity);
}
