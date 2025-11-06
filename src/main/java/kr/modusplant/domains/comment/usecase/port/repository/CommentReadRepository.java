package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.response.CommentOfAuthorResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;

import java.util.List;

public interface CommentReadRepository {

    List<CommentOfPostResponse> findByPost(PostId postId);

    List<CommentOfAuthorResponse> findByAuthor(Author author);
}
