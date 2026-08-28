package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;

public interface CommentCommandRepository {

    void save(Comment comment);

    void updateContent(PostId postId, CommentPath path, CommentContent content);

    void setCommentAsDeleted(PostId postId, CommentPath path);
}
