package kr.modusplant.domains.comment.adapter.helper;

import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.domain.exception.DeletedReplyToCommentException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.port.repository.ImmediateParentPostRepository;
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
    private final ImmediateParentPostRepository immediateParentPostRepository;

    public void validateIfAuthorExists(CommentAuthorId commentAuthorId) {
        if (!authorRepository.isExists(commentAuthorId)) {
            throw new NotFoundValueException(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR, "commentAuthor");
        }
    }

    public void validateIfImmediateParentPostExists(ImmediateParentPostId postUlid) {
        if (!immediateParentPostRepository.isImmediateParentPostExists(postUlid)) {
            throw new NotFoundValueException(CommentErrorCode.NOT_FOUND_IMMEDIATE_PARENT_POST, "immediateParentPost");
        }
    }

    public void validateIfImmediateParentPostIsPublished(ImmediateParentPostId immediateParentPostId) {
        if (!immediateParentPostRepository.isImmediateParentPostPublished(immediateParentPostId)) {
            throw new InvalidValueException(CommentErrorCode.NOT_PUBLISHED_POST, "immediateParentPost");
        }
    }

    public void validateIfCommentExists(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        if (!queryRepository.isCommentExists(immediateParentPostId, path)) {
            throw new NotFoundValueException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT);
        }
    }

    public void validateIfCommentIsNotDeleted(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        if (queryRepository.isCommentDeleted(immediateParentPostId, path)) {
            throw new DeletedReplyToCommentException();
        }
    }

    public void validateIfCommentWrittenByAuthor(ImmediateParentPostId immediateParentPostId, CommentPath path, CommentAuthorId commentAuthorId) {
        if (!queryRepository.isCommentWrittenByAuthor(immediateParentPostId, path, commentAuthorId)) {
            throw new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "commentAuthorId", path.getValue());
        }
    }

    public void validateIfAuthorCanWriteWithinPost(ImmediateParentPostId immediateParentPostId, CommentAuthorId commentAuthorId) {
        validateIfAuthorExists(commentAuthorId);
        validateIfImmediateParentPostExists(immediateParentPostId);
        validateIfImmediateParentPostIsPublished(immediateParentPostId);
    }

    public void validateIfAuthorCanWriteCommentWithinPost(ImmediateParentPostId immediateParentPostId, CommentPath path, CommentAuthorId commentAuthorId) {
        validateIfAuthorExists(commentAuthorId);
        validateIfImmediateParentPostExists(immediateParentPostId);
        validateIfImmediateParentPostIsPublished(immediateParentPostId);
        validateIfCommentExists(immediateParentPostId, path);
        validateIfCommentWrittenByAuthor(immediateParentPostId, path, commentAuthorId);
    }
}
