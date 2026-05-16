package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.framework.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SecondaryCategoryEntity;

public interface PostJpaMapper {
    PostEntity toPostEntity(Post post, MemberEntity authorEntity, PrimaryCategoryEntity primaryCategoryEntity, SecondaryCategoryEntity secondaryCategoryEntity, Long viewCount);

    Post toPost(PostEntity postEntity);
}
