package kr.modusplant.domains.comment.domain.aggregate;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {
    private final PostId postId;
    private final CommentPath path;
    private final Author author;
    private final CommentContent content;
    private final CommentStatus status;

    public static Comment create(PostId postId, CommentPath path, Author author, CommentContent content) {
        if(postId == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID); }
        if(path == null) { throw new EmptyValueException(CommentErrorCode.INVALID_COMMENT_PATH); }
        if(author == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR); }
        if(content == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT); }
        return new Comment(postId, path, author, content, CommentStatus.setAsValid());
    }

    public static Comment create(PostId postId, CommentPath path, Author author, CommentContent content, CommentStatus status) {
        if(postId == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID); }
        if(path == null) { throw new EmptyValueException(CommentErrorCode.INVALID_COMMENT_PATH); }
        if(author == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR); }
        if(content == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT); }
        if (status == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_STATUS); }

        return new Comment(postId, path, author, content, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Comment comment)) return false;

        return new EqualsBuilder()
                .append(getPostId(), comment.getPostId())
                .append(getPath(), comment.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostId()).append(getPath()).toHashCode();
    }
}
