package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.framework.out.jpa.entity.AuthorEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;

public interface PostJpaMapper {
    PostEntity toPostEntity(Post post, AuthorEntity authorEntity, AuthorEntity createAuthorEntity, PrimaryCategoryEntity primaryCategoryEntity, SecondaryCategoryEntity secondaryCategoryEntity, Long viewCount);

    Post toPost(PostEntity postEntity);
}
