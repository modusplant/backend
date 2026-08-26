package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;

public interface CommentCommandRepository {

    void save(Comment comment);

    void update(PostId postId, CommentPath path, CommentContent content);

    void setCommentAsDeleted(PostId postId, CommentPath path);
}
