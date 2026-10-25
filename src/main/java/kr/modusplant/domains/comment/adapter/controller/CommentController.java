package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.helper.CommentValidationHelper;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.event.CommentRegisterEvent;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.*;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCommandRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.comment.usecase.response.CommentCursorPageResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOffsetPageResponse;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CommentController {
    private final CommentMapper mapper;
    private final CommentQueryRepository queryRepository;
    private final CommentCommandRepository commandRepository;
    private final CommentCacheRepository cacheRepository;
    private final CommentValidationHelper commentValidationHelper;
    private final SwearHolder swearHolder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public List<CommentOfPostResponse> gatherByPost_V1(String postUlid, UUID authorId) {
        commentValidationHelper.validateIfParentPostExists(ParentPostId.create(postUlid));

        return queryRepository.findByPost(ParentPostId.create(postUlid), CommentAuthorId.fromNullableUuid(authorId))
                .stream().map(mapper::toCommentOfPostResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentCursorPageResponse gatherByPost_V2(String postUlid, String lastPath, Integer size, UUID authorId) {
        ParentPostId parentPostId = ParentPostId.create(postUlid);
        CommentPageSize pageSize = CommentPageSize.create(size);
        CommentAuthorId commentAuthorId = CommentAuthorId.fromNullableUuid(authorId);
        commentValidationHelper.validateIfParentPostExists(parentPostId);

        List<CommentOfPostResponse> responses;
        if (lastPath != null) { // 특정 lastPath 이후부터 조회
            CommentPath commentPath = CommentPath.create(lastPath);
            commentValidationHelper.validateIfCommentExists(parentPostId, commentPath);
            responses = queryRepository.findByPost(parentPostId, commentPath, pageSize, commentAuthorId)
                    .stream().map(mapper::toCommentOfPostResponse)
                    .toList();
        } else { // 첫 댓글 데이터부터 조회
            responses = queryRepository.findByPost(parentPostId, null, pageSize, commentAuthorId)
                    .stream().map(mapper::toCommentOfPostResponse)
                    .toList();
        }

        if (responses.size() > pageSize.getValue()) { // hasNext == true
            List<CommentOfPostResponse> returnedResponses = responses.subList(0, pageSize.getValue());
            return CommentCursorPageResponse.of(
                    returnedResponses, returnedResponses.getLast().path(), true);
        } else { // hasNext == false
            if (!responses.isEmpty()) {
                return CommentCursorPageResponse.of(
                        responses, responses.getLast().path(), false);
            } else {
                return CommentCursorPageResponse.of(
                        responses, null, false);
            }
        }
    }

    @Transactional(readOnly = true)
    public CommentOffsetPageResponse<CommentOfAuthorReadModel> gatherByAuthor(UUID authorId, Pageable pageable) {
        commentValidationHelper.validateIfAuthorExists(CommentAuthorId.fromUuid(authorId));

        Page<CommentOfAuthorReadModel> result = queryRepository.findByAuthor(CommentAuthorId.fromUuid(authorId), pageable);
        return mapper.toCommentPageResponseWithOnePlusPage(result);
    }

    public void register(CommentRegisterRequest request, UUID authorId) {
        ParentPostId parentPostId = ParentPostId.create(request.postId());
        CommentPath path = CommentPath.create(request.path());
        ReplyToCommentPath replyToPath = ReplyToCommentPath.create(request.replyToPath());
        CommentAuthorId commentAuthorId = CommentAuthorId.fromUuid(authorId);
        commentValidationHelper.validateIfAuthorCanWriteWithinPost(parentPostId, commentAuthorId);
        validateIfParentCommentExists(parentPostId, path);
        if (!(replyToPath instanceof EmptyReplyToCommentPath)) {
            validateIfReplyToCommentPathIsValid(parentPostId, path, replyToPath);
        }

        // 멱등성 검증 후, 같은 부모 아래의 다음 형제 경로 순서 값을 Redis에서 예약받는다.
        // DB 하한값 조회(findMaximumSiblingPathOrder)는 Redis 예약 키가 전혀 없을 때만 실행되도록 지연 전달한다.
        Optional<CommentPath> optionalReservedPath =
                cacheRepository.reservePath(parentPostId, path, commentAuthorId,
                        () -> queryRepository.findMaximumSiblingPathOrder(parentPostId, path));
        if (optionalReservedPath.isEmpty()) {
            return;
        }
        CommentPath reservedPath = optionalReservedPath.get();

        Comment comment = Comment.create(
                parentPostId, reservedPath, replyToPath, commentAuthorId, CommentContent.create(swearHolder.filterSwear(request.content())));
        commandRepository.save(comment);

        applicationEventPublisher.publishEvent(
                CommentRegisterEvent.create(authorId, request.postId(), reservedPath.getValue(), request.content())
        );
    }

    public void updateContent(CommentUpdateRequest request, UUID authorId) {
        ParentPostId parentPostId = ParentPostId.create(request.postId());
        CommentPath path = CommentPath.create(request.path());
        CommentAuthorId commentAuthorId = CommentAuthorId.fromUuid(authorId);
        commentValidationHelper.validateIfAuthorCanWriteCommentWithinPost(parentPostId, path, commentAuthorId);

        commandRepository.updateContent(parentPostId, path, CommentContent.create(request.content()));
    }

    public void delete(String postUlid, String commentPath, UUID authorId) {
        ParentPostId parentPostIdVo = ParentPostId.create(postUlid);
        CommentPath commentPathVO = CommentPath.create(commentPath);
        CommentAuthorId commentAuthorId = CommentAuthorId.fromUuid(authorId);
        commentValidationHelper.validateIfAuthorCanWriteCommentWithinPost(parentPostIdVo, commentPathVO, commentAuthorId);

        commandRepository.deleteComment(ParentPostId.create(postUlid), CommentPath.create(commentPath));
    }

    private void validateIfParentCommentExists(ParentPostId parentPostId, CommentPath path) {
        String pathValue = path.getValue();
        if (!pathValue.contains(".")) {
            return;
        }
        String parentCommentPath = pathValue.substring(0, pathValue.lastIndexOf("."));
        if (!queryRepository.isCommentExists(parentPostId, CommentPath.create(parentCommentPath))) {
            throw new InvalidValueException(CommentErrorCode.NOT_FOUND_PARENT_COMMENT, "commentPath");
        }
    }

    private void validateIfReplyToCommentPathIsValid(ParentPostId parentPostId, CommentPath path, ReplyToCommentPath replyToPath) {
        String[] pathSegments = path.getValue().split("\\.");
        String[] replyToPathSegments = replyToPath.getValue().split("\\.");
        if (replyToPathSegments.length != 2
                || pathSegments.length != 2
                || !replyToPathSegments[0].equals(pathSegments[0])) {
            throw new InvalidValueException(CommentErrorCode.INVALID_REPLY_TO_COMMENT_PATH, "replyToPath");
        }
        CommentPath replyToCommentVO = CommentPath.create(replyToPath.getValue());
        commentValidationHelper.validateIfCommentExists(parentPostId, replyToCommentVO);
        commentValidationHelper.validateIfCommentIsNotDeleted(parentPostId, replyToCommentVO);
    }
}
