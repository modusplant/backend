package kr.modusplant.domains.comment.adapter.helper;

import kr.modusplant.domains.comment.domain.exception.DeletedReplyToCommentException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentAuthor;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;
import kr.modusplant.domains.comment.framework.outbound.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidationHelper {
    private final CommentQueryRepository queryRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    public void validateIfAuthorExists(CommentAuthor commentAuthor) {
        if (!memberJpaRepository.existsById(commentAuthor.getUuid())) {
            throw new NotFoundEntityException(CommentErrorCode.NOT_EXIST_AUTHOR, "author");
        }
    }

    public void validateIfPostExists(ImmediateParentPostId postUlid) {
        if (!postJpaRepository.existsByUlid(postUlid.getValue())) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post");
        }
    }

    public void validateIfPostIsPublished(ImmediateParentPostId immediateParentPostId) {
        if (!queryRepository.isPostPublished(immediateParentPostId)) {
            throw new InvalidValueException(CommentErrorCode.NOT_PUBLISHED_POST, "post");
        }
    }

    public void validateIfCommentExists(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        if (!queryRepository.isCommentExists(immediateParentPostId, path)) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT);
        }
    }

    public void validateIfCommentIsNotDeleted(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        if (queryRepository.isCommentDeleted(immediateParentPostId, path)) {
            throw new DeletedReplyToCommentException();
        }
    }

    public void validateIfCommentWrittenByAuthor(ImmediateParentPostId immediateParentPostId, CommentPath path, CommentAuthor commentAuthor) {
        if (!commentJpaRepository.existsByPostUlidAndPathAndAuthMemberUuid(immediateParentPostId.getValue(), path.getValue(), commentAuthor.getUuid())) {
            throw new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "comment", path.getValue());
        }
    }

    public void validateIfAuthorCanWriteWithinPost(ImmediateParentPostId immediateParentPostId, CommentAuthor commentAuthor) {
        validateIfAuthorExists(commentAuthor);
        validateIfPostExists(immediateParentPostId);
        validateIfPostIsPublished(immediateParentPostId);
    }

    public void validateIfAuthorCanWriteCommentWithinPost(ImmediateParentPostId immediateParentPostId, CommentPath path, CommentAuthor commentAuthor) {
        validateIfAuthorExists(commentAuthor);
        validateIfPostExists(immediateParentPostId);
        validateIfPostIsPublished(immediateParentPostId);
        validateIfCommentExists(immediateParentPostId, path);
        validateIfCommentWrittenByAuthor(immediateParentPostId, path, commentAuthor);
    }
}
