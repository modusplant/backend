package kr.modusplant.domains.comment.adapter.helper;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
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

    public void validateIfAuthorExists(Author author) {
        if (!memberJpaRepository.existsById(author.getUuid())) {
            throw new NotFoundEntityException(CommentErrorCode.NOT_EXIST_AUTHOR, "author");
        }
    }

    public void validateIfPostExists(PostId postUlid) {
        if (!postJpaRepository.existsByUlid(postUlid.getValue())) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post");
        }
    }

    public void validateIfPostIsPublished(PostId postId) {
        if (!queryRepository.isPostPublished(postId)) {
            throw new InvalidValueException(CommentErrorCode.NOT_PUBLISHED_POST, "post");
        }
    }

    public void validateIfCommentExists(PostId postId, CommentPath path) {
        if (!queryRepository.isCommentExists(postId, path)) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT);
        }
    }

    public void validateIfCommentWrittenByAuthor(PostId postId, CommentPath path, Author author) {
        if (!commentJpaRepository.existsByPostUlidAndPathAndAuthMemberUuid(postId.getValue(), path.getValue(), author.getUuid())) {
            throw new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "comment", path.getValue());
        }
    }

    public void validateIfAuthorCanWriteWithinPost(PostId postId, Author author) {
        validateIfAuthorExists(author);
        validateIfPostExists(postId);
        validateIfPostIsPublished(postId);
    }

    public void validateIfAuthorCanWriteCommentWithinPost(PostId postId, CommentPath path, Author author) {
        validateIfAuthorExists(author);
        validateIfPostExists(postId);
        validateIfPostIsPublished(postId);
        validateIfCommentExists(postId, path);
        validateIfCommentWrittenByAuthor(postId, path, author);
    }
}
