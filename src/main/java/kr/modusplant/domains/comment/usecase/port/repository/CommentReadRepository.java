package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentReadRepository {

    List<CommentOfPostReadModel> findByPost(PostId postId, Author author);

    PageImpl<CommentOfAuthorPageModel> findByAuthor(Author author, Pageable pageable);

    boolean existsByPostAndPath(PostId postId, CommentPath path);

    int countPostComment(PostId postId);

    Optional<LocalDateTime> findLatestUpdatedAtByPost(PostId postId);
}
