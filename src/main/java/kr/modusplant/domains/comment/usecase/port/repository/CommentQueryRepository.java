package kr.modusplant.domains.comment.usecase.port.repository;

import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.domain.vo.CommentPageSize;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ParentPostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentQueryRepository {

    List<CommentOfPostReadModel> findByPost(ParentPostId parentPostId, CommentAuthorId commentAuthorId);

    /**
     * {@code lastPath} 댓글 이후(첫 페이지면 처음)부터 다음 페이지 존재 여부 판단을 위해
     * {@code pageSize} 보다 1개 많은 댓글을 조회한다.
     */
    List<CommentOfPostReadModel> findByPost(ParentPostId parentPostId, @Nullable CommentPath lastPath, CommentPageSize pageSize, CommentAuthorId commentAuthorId);

    PageImpl<CommentOfAuthorReadModel> findByAuthor(CommentAuthorId commentAuthorId, Pageable pageable);

    Optional<LocalDateTime> findLatestUpdatedAtByPost(ParentPostId parentPostId);

    Optional<LocalDateTime> findLatestUpdatedAtByAuthor(CommentAuthorId commentAuthorId);

    /**
     * {@code path} 와 같은 부모(마지막 {@code .} 이전, 부모가 없으면 최상위) 아래에 이미 존재하는
     * 형제 댓글들의 마지막 세그먼트 중 가장 큰 값을 반환한다. 형제가 없으면 {@code 0}을 반환한다.
     */
    int findMaximumSiblingPathOrder(ParentPostId parentPostId, CommentPath path);

    boolean isCommentExists(ParentPostId parentPostId, CommentPath path);

    boolean isCommentDeleted(ParentPostId parentPostId, CommentPath path);

    boolean isCommentWrittenByAuthor(ParentPostId parentPostId, CommentPath path, CommentAuthorId commentAuthorId);
}
