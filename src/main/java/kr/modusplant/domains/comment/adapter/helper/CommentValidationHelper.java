package kr.modusplant.domains.comment.adapter.helper;

import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.domain.exception.DeletedReplyToCommentException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ParentPostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.port.repository.ParentPostRepository;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.NotFoundValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidationHelper {
    private final CommentQueryRepository queryRepository;
    private final CommentAuthorRepository authorRepository;
    private final ParentPostRepository parentPostRepository;

    public void validateIfAuthorExists(CommentAuthorId commentAuthorId) {
        if (!authorRepository.isExists(commentAuthorId)) {
            throw new NotFoundValueException(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR, "commentAuthor");
        }
    }

    public void validateIfParentPostExists(ParentPostId postUlid) {
        if (!parentPostRepository.isParentPostExists(postUlid)) {
            throw new NotFoundValueException(CommentErrorCode.NOT_FOUND_PARENT_POST, "parentPost");
        }
    }

    public void validateIfParentPostIsPublished(ParentPostId parentPostId) {
        if (!parentPostRepository.isParentPostPublished(parentPostId)) {
            throw new InvalidValueException(CommentErrorCode.NOT_PUBLISHED_POST, "parentPost");
        }
    }

    public void validateIfCommentExists(ParentPostId parentPostId, CommentPath path) {
        if (!queryRepository.isCommentExists(parentPostId, path)) {
            throw new NotFoundValueException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT);
        }
    }

    public void validateIfCommentIsNotDeleted(ParentPostId parentPostId, CommentPath path) {
        if (queryRepository.isCommentDeleted(parentPostId, path)) {
            throw new DeletedReplyToCommentException();
        }
    }

    public void validateIfCommentWrittenByAuthor(ParentPostId parentPostId, CommentPath path, CommentAuthorId commentAuthorId) {
        if (!queryRepository.isCommentWrittenByAuthor(parentPostId, path, commentAuthorId)) {
            throw new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "commentAuthorId", path.getValue());
        }
    }

    public void validateIfAuthorCanWriteWithinPost(ParentPostId parentPostId, CommentAuthorId commentAuthorId) {
        validateIfAuthorExists(commentAuthorId);
        validateIfParentPostExists(parentPostId);
        validateIfParentPostIsPublished(parentPostId);
    }

    public void validateIfAuthorCanWriteCommentWithinPost(ParentPostId parentPostId, CommentPath path, CommentAuthorId commentAuthorId) {
        validateIfAuthorExists(commentAuthorId);
        validateIfParentPostExists(parentPostId);
        validateIfParentPostIsPublished(parentPostId);
        validateIfCommentExists(parentPostId, path);
        validateIfCommentWrittenByAuthor(parentPostId, path, commentAuthorId);
    }
}
