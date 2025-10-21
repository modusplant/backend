package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostJpaMapperImpl implements PostJpaMapper {

    @Override
    public PostEntity toPostEntity(Post post, SiteMemberEntity authorEntity, SiteMemberEntity createAuthorEntity, CommPrimaryCategoryEntity primaryCategoryEntity, CommSecondaryCategoryEntity secondaryCategoryEntity, Long viewCount) {
        LocalDateTime publishedAt = post.getStatus().isPublished() ? LocalDateTime.now() : null;
        return PostEntity.builder()
                .ulid(post.getPostId().getValue())
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(authorEntity)
                .createMember(createAuthorEntity)
                .likeCount(post.getLikeCount().getValue())
                .viewCount(viewCount)
                .title(post.getPostContent().getTitle())
                .content(post.getPostContent().getContent())
                .isPublished(post.getStatus().isPublished())
                .publishedAt(publishedAt)
                .build();
    }

    @Override
    public Post toPost(PostEntity postEntity) {
        PostStatus postStatus;
        if (postEntity.getIsPublished()) {
            postStatus = PostStatus.published();
        } else {
            postStatus = PostStatus.draft();
        }
        return Post.create(
                PostId.create(postEntity.getUlid()),
                AuthorId.fromUuid(postEntity.getAuthMember().getUuid()),
                PrimaryCategoryId.fromUuid(postEntity.getPrimaryCategory().getUuid()),
                SecondaryCategoryId.fromUuid(postEntity.getSecondaryCategory().getUuid()),
                PostContent.create(
                        postEntity.getTitle(),
                        postEntity.getContent()
                ),
                LikeCount.create(postEntity.getLikeCount()),
                postStatus
        );
    }

}
