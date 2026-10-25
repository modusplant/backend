package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;

public interface CommentCommandRepository {

    void save(Comment comment);

    void updateContent(ImmediateParentPostId immediateParentPostId, CommentPath path, CommentContent content);

    void deleteComment(ImmediateParentPostId immediateParentPostId, CommentPath path);
}
