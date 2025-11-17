package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;

public interface PostArchiveJpaMapper {
    CommPostArchiveEntity toPostArchiveEntity(CommPostEntity postEntity);
}
