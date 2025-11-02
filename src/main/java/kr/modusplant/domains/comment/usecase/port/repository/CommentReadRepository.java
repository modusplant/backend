package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;

import java.util.List;

public interface CommentReadRepository {

    List<CommentResponse> findByPost(PostId postId);

    List<CommentResponse> findByAuthor(Author author);
}
