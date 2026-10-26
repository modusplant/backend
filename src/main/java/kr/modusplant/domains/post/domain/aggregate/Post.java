package kr.modusplant.domains.post.domain.aggregate;

import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {
    private final PostId postId;
    private PostAuthorId postAuthorId;
    private PrimaryCategoryId primaryCategoryId;
    private SecondaryCategoryId secondaryCategoryId;
    private PostContent postContent;
    private PostLikeCount postLikeCount;
    private PostStatus status;

    public static Post create(PostId postId, PostAuthorId postAuthorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent, PostLikeCount postLikeCount, PostStatus postStatus) {
        if(postId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_ID);
        } else if (postAuthorId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        } else if(primaryCategoryId == null && secondaryCategoryId != null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (postContent == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
        } else if (postLikeCount == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_LIKE_COUNT);
        } else if (postStatus == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_STATUS);
        }
        return new Post(postId, postAuthorId, primaryCategoryId, secondaryCategoryId, postContent, postLikeCount, postStatus);
    }

    public static Post createDraft(PostAuthorId postAuthorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent) {
        if (postAuthorId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        } else if (primaryCategoryId == null && secondaryCategoryId != null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (postContent == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
        }
        return new Post(null, postAuthorId, primaryCategoryId, secondaryCategoryId, postContent, PostLikeCount.zero(), PostStatus.draft());
    }

    public static Post createPublished(PostAuthorId postAuthorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent) {
        if (postAuthorId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        } else if (primaryCategoryId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (secondaryCategoryId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (postContent == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
        }
        return new Post(null, postAuthorId, primaryCategoryId, secondaryCategoryId, postContent, PostLikeCount.zero(), PostStatus.published());
    }

    public void update(PostAuthorId postAuthorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent, PostStatus postStatus) {
        if (postAuthorId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        } else if (primaryCategoryId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (secondaryCategoryId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (postContent == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
        } else if (postStatus == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_STATUS);
        }
        if(this.status.isPublished() && postStatus.isDraft()) {
            throw new InvalidValueException(PostErrorCode.INVALID_POST_STATUS);
        }
        this.postAuthorId = postAuthorId;
        this.primaryCategoryId = primaryCategoryId;
        this.secondaryCategoryId = secondaryCategoryId;
        this.postContent = postContent;
        this.status = postStatus;
    }

    public void updateDraft(PostAuthorId postAuthorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent) {
        if (postAuthorId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        } else if(primaryCategoryId == null && secondaryCategoryId != null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (postContent == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
        } else if (this.status.isPublished()) {
            throw new InvalidValueException(PostErrorCode.INVALID_POST_STATUS);
        }
        this.postAuthorId = postAuthorId;
        this.primaryCategoryId = primaryCategoryId;
        this.secondaryCategoryId = secondaryCategoryId;
        this.postContent = postContent;
        this.status = PostStatus.draft();
    }

    public void updateAuthorId(PostAuthorId newPostAuthorId) {
        if (newPostAuthorId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_AUTHOR_ID);
        }
        this.postAuthorId = newPostAuthorId;
    }

    public void updateContent(PostContent newContent) {
        this.postContent = newContent;
    }

    /* 상태 전이 */
    public void publish() {
        if (this.status.isPublished()) {
            throw new InvalidValueException(PostErrorCode.INVALID_POST_STATUS);
        }
        if (this.primaryCategoryId == null || this.secondaryCategoryId == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        }
        if (this.postContent == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
        }
        this.status = PostStatus.published();
    }

    public void like() {
        if (this.status.isDraft()) {
            throw new InvalidValueException(PostErrorCode.INVALID_POST_STATUS);
        }
        this.postLikeCount = this.postLikeCount.increment();
    }

    public void unlike() {
        if (this.status.isDraft()) {
            throw new InvalidValueException(PostErrorCode.INVALID_POST_STATUS);
        }
        this.postLikeCount = postLikeCount.decrement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Post post)) return false;

        return new EqualsBuilder().append(getPostId(), post.getPostId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getPostId()).toHashCode();
    }

}
