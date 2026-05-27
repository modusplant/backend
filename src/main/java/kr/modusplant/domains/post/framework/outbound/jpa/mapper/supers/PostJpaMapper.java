package kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;

public interface PostJpaMapper {
    PostEntity toPostEntity(Post post, MemberEntity authorEntity, PrimaryCategoryEntity primaryCategoryEntity, SecondaryCategoryEntity secondaryCategoryEntity, Long viewCount);

    Post toPost(PostEntity postEntity);
}
