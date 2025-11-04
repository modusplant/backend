package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;

public interface PostJpaMapper {
    CommPostEntity toPostEntity(Post post, SiteMemberEntity authorEntity, SiteMemberEntity createAuthorEntity, CommPrimaryCategoryEntity primaryCategoryEntity, CommSecondaryCategoryEntity secondaryCategoryEntity, Long viewCount);

    Post toPost(CommPostEntity postEntity);

    PostSummaryReadModel toPostSummaryReadModel(CommPostEntity postEntity);

    PostDetailReadModel toPostDetailReadModel(CommPostEntity postEntity);
}
