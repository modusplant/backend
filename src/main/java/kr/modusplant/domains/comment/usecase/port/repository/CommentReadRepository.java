package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.response.CommentOfAuthorResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentReadRepository {

    List<CommentOfPostResponse> findByPost(PostId postId);

    PageImpl<CommentOfAuthorResponse> findByAuthor(Author author, Pageable pageable);
}
