package kr.modusplant.domains.comment.domain.aggregate;

import kr.modusplant.domains.comment.domain.vo.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {
    private PostId postId;
    private CommentPath path;
    private Author author;
    private CommentContent content;
    private CommentStatus status;

    // TODO: PostId는 게시글 담당자가 개발한 PostId VO로 대체될 예정
    public static Comment create(PostId postId, CommentPath path, Author author, CommentContent content) {
        return new Comment(postId, path, author, content, CommentStatus.setAsValid());
    }

    public static Comment create(PostId postId, CommentPath path, Author author, CommentContent content, CommentStatus status) {
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
