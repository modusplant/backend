package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentQueryRepository {

    List<CommentOfPostReadModel> findByPost(PostId postId, Author author);

    PageImpl<CommentOfAuthorReadModel> findByAuthor(Author author, Pageable pageable);

    Optional<LocalDateTime> findLatestUpdatedAtByPost(PostId postId);

    /**
     * {@code path} 와 같은 부모(마지막 {@code .} 이전, 부모가 없으면 최상위) 아래에 이미 존재하는
     * 형제 댓글들의 마지막 세그먼트 중 가장 큰 값을 반환한다. 형제가 없으면 {@code 0}을 반환한다.
     */
    int findMaximumSiblingPathOrder(PostId postId, CommentPath path);

    int countPostComment(PostId postId);

    boolean isCommentExists(PostId postId, CommentPath path);

    boolean isPostPublished(PostId postId);
}
