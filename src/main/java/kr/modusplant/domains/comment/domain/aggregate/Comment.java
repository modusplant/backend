package kr.modusplant.domains.comment.domain.aggregate;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.*;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyCommentAuthorId;
import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {
    private final ParentPostId parentPostId;
    private final CommentPath path;
    private final ReplyToCommentPath replyToPath;
    private final CommentAuthorId commentAuthorId;
    private final CommentContent content;
    private final CommentStatus status;

    public static Comment create(ParentPostId parentPostId, CommentPath path, ReplyToCommentPath replyToPath, CommentAuthorId commentAuthorId, CommentContent content) {
        if(parentPostId == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID, "postId"); }
        if(path == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH, "path"); }
        if(replyToPath == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_REPLY_TO_COMMENT_PATH, "replyToPath"); }
        if(commentAuthorId == null || commentAuthorId instanceof EmptyCommentAuthorId) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_AUTHOR, "commentAuthorId"); }
        if(content == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT, "content"); }
        return new Comment(parentPostId, path, replyToPath, commentAuthorId, content, CommentStatus.active());
    }

    public static Comment create(ParentPostId parentPostId, CommentPath path, ReplyToCommentPath replyToPath, CommentAuthorId commentAuthorId, CommentContent content, CommentStatus status) {
        if(parentPostId == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID, "postId"); }
        if(path == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH, "path"); }
        if(replyToPath == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_REPLY_TO_COMMENT_PATH, "replyToPath"); }
        if(commentAuthorId == null || commentAuthorId instanceof EmptyCommentAuthorId) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_AUTHOR, "commentAuthorId"); }
        if(content == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT, "content"); }
        if(status == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_STATUS, "status"); }
        return new Comment(parentPostId, path, replyToPath, commentAuthorId, content, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Comment comment)) return false;

        return new EqualsBuilder()
                .append(getParentPostId(), comment.getParentPostId())
                .append(getPath(), comment.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getParentPostId()).append(getPath()).toHashCode();
    }
}
