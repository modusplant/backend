package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;

public interface PostJpaMapper {
    PostEntity toPostEntity(Post post, SiteMemberEntity authorEntity, SiteMemberEntity createAuthorEntity, CommPrimaryCategoryEntity primaryCategoryEntity, CommSecondaryCategoryEntity secondaryCategoryEntity, Long viewCount);

    Post toPost(PostEntity postEntity);

    PostSummaryReadModel toPostSummaryReadModel(PostEntity postEntity);

    PostDetailReadModel toPostDetailReadModel(PostEntity postEntity);
}
