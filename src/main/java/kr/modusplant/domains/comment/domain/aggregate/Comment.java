package kr.modusplant.domains.comment.domain.aggregate;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.*;
import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {
    private final ImmediateParentPostId immediateParentPostId;
    private final CommentPath path;
    private final ReplyToCommentPath replyToPath;
    private final CommentAuthor commentAuthor;
    private final CommentContent content;
    private final CommentStatus status;

    public static Comment create(ImmediateParentPostId immediateParentPostId, CommentPath path, ReplyToCommentPath replyToPath, CommentAuthor commentAuthor, CommentContent content) {
        if(immediateParentPostId == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID, "postId"); }
        if(path == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH, "path"); }
        if(replyToPath == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_REPLY_TO_COMMENT_PATH, "replyToPath"); }
        if(commentAuthor == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR, "author"); }
        if(content == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT, "content"); }
        return new Comment(immediateParentPostId, path, replyToPath, commentAuthor, content, CommentStatus.active());
    }

    public static Comment create(ImmediateParentPostId immediateParentPostId, CommentPath path, ReplyToCommentPath replyToPath, CommentAuthor commentAuthor, CommentContent content, CommentStatus status) {
        if(immediateParentPostId == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID, "postId"); }
        if(path == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH, "path"); }
        if(replyToPath == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_REPLY_TO_COMMENT_PATH, "replyToPath"); }
        if(commentAuthor == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR, "author"); }
        if(content == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT, "content"); }
        if(status == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_STATUS, "status"); }
        return new Comment(immediateParentPostId, path, replyToPath, commentAuthor, content, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Comment comment)) return false;

        return new EqualsBuilder()
                .append(getImmediateParentPostId(), comment.getImmediateParentPostId())
                .append(getPath(), comment.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getImmediateParentPostId()).append(getPath()).toHashCode();
    }
}
