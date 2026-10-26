package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ParentPostId;

public interface CommentCommandRepository {

    void save(Comment comment);

    void updateContent(ParentPostId parentPostId, CommentPath path, CommentContent content);

    void deleteComment(ParentPostId parentPostId, CommentPath path);
}
