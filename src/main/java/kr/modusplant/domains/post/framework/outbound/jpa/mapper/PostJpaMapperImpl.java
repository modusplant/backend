package kr.modusplant.domains.post.framework.outbound.jpa.mapper;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity.PostEntityBuilder;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers.PostJpaMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostJpaMapperImpl implements PostJpaMapper {

    @Override
    public PostEntity toPostEntity(Post post, MemberEntity authorEntity, PrimaryCategoryEntity primaryCategoryEntity, SecondaryCategoryEntity secondaryCategoryEntity, Long viewCount) {
        LocalDateTime publishedAt = post.getStatus().isPublished() ? LocalDateTime.now() : null;
        PostEntityBuilder postEntityBuilder = PostEntity.builder();
        if (post.getPostId() != null) {
            postEntityBuilder.ulid(post.getPostId().getValue());
        }
        return postEntityBuilder
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(authorEntity)
                .likeCount(post.getLikeCount().getValue())
                .viewCount(viewCount)
                .title(post.getPostContent().getTitle())
                .content(post.getPostContent().getContent())
                .thumbnailPath(post.getPostContent().getThumbnailPath())
                .isPublished(post.getStatus().isPublished())
                .publishedAt(publishedAt)
                .build();
    }

    @Override
    public Post toPost(PostEntity postEntity) {
        if (postEntity.getAuthMember() == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        }
        PostStatus postStatus;
        PostContent content;
        if (postEntity.getIsPublished()) {
            postStatus = PostStatus.published();
            content = PostContent.create(postEntity.getTitle(), postEntity.getContent(), postEntity.getThumbnailPath());
        } else {
            postStatus = PostStatus.draft();
            content = PostContent.createDraft(postEntity.getTitle(), postEntity.getContent(), postEntity.getThumbnailPath());
        }
        return Post.create(
                PostId.create(postEntity.getUlid()),
                AuthorId.fromUuid(postEntity.getAuthMember().getUuid()),
                postEntity.getPrimaryCategory() != null ? PrimaryCategoryId.create(postEntity.getPrimaryCategory().getId()) : null,
                postEntity.getSecondaryCategory() != null ? SecondaryCategoryId.create(postEntity.getSecondaryCategory().getId()) : null,
                content,
                LikeCount.create(postEntity.getLikeCount()),
                postStatus
        );
    }

}
