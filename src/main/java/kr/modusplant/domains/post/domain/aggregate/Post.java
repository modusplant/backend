package kr.modusplant.domains.post.domain.aggregate;

import kr.modusplant.domains.post.domain.exception.*;
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
    private AuthorId authorId;
    private AuthorId createAuthorId;
    private PrimaryCategoryId primaryCategoryId;
    private SecondaryCategoryId secondaryCategoryId;
    private PostContent postContent;
    private LikeCount likeCount;
    private PostStatus status;

    public static Post create(PostId postId, AuthorId authorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent, LikeCount likeCount, PostStatus postStatus) {
        if(postId == null) {
            throw new EmptyPostIdException();
        } else if (authorId == null) {
            throw new EmptyAuthorIdException();
        } else if (primaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (secondaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (postContent == null) {
            throw new EmptyPostContentException();
        } else if (likeCount == null) {
            throw new EmptyLikeCountException();
        } else if (postStatus == null) {
            throw new EmptyPostStatusException();
        }
        return new Post(postId, authorId, authorId, primaryCategoryId, secondaryCategoryId, postContent, likeCount, postStatus);
    }

    public static Post createDraft(AuthorId authorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent) {
        if (authorId == null) {
            throw new EmptyAuthorIdException();
        } else if (primaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (secondaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (postContent == null) {
            throw new EmptyPostContentException();
        }
        return new Post(null, authorId, authorId, primaryCategoryId, secondaryCategoryId, postContent, LikeCount.zero(), PostStatus.draft());
    }

    public static Post createPublished(AuthorId authorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent) {
        if (authorId == null) {
            throw new EmptyAuthorIdException();
        } else if (primaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (secondaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (postContent == null) {
            throw new EmptyPostContentException();
        }
        return new Post(null, authorId, authorId, primaryCategoryId, secondaryCategoryId, postContent, LikeCount.zero(), PostStatus.published());
    }

    public void update(AuthorId authorId, PrimaryCategoryId primaryCategoryId, SecondaryCategoryId secondaryCategoryId, PostContent postContent, PostStatus postStatus) {
        if (authorId == null) {
            throw new EmptyAuthorIdException();
        } else if (primaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (secondaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        } else if (postContent == null) {
            throw new EmptyPostContentException();
        } else if (postStatus == null) {
            throw new EmptyPostStatusException();
        }
        if(this.status.isPublished() && postStatus.isDraft()) {
            throw new InvalidPostStatusException();
        }
        this.authorId = authorId;
        this.primaryCategoryId = primaryCategoryId;
        this.secondaryCategoryId = secondaryCategoryId;
        this.postContent = postContent;
        this.status = postStatus;
    }

    public void updateAuthorId(AuthorId newAuthorId) {
        if (newAuthorId == null) {
            throw new EmptyAuthorIdException();
        }
        this.authorId = newAuthorId;
    }

    public void updateContent(PostContent newContent) {
        this.postContent = newContent;
    }

    public void publish() {
        if (this.status.isPublished()) {
            throw new InvalidPostStatusException();
        }
        this.status = PostStatus.published();
    }

    public void like() {
        if (this.status.isDraft()) {
            throw new InvalidPostStatusException();
        }
        this.likeCount = this.likeCount.increment();
    }

    public void unlike() {
        if (this.status.isDraft()) {
            throw new InvalidPostStatusException();
        }
        this.likeCount = likeCount.decrement();
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
