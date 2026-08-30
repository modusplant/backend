package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.helper.CommentValidationHelper;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.event.CommentRegisterEvent;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCommandRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.InvalidValueException;
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
    private final SwearService swearService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public List<CommentOfPostResponse> gatherByPost(String postUlid, UUID authorId) {
        commentValidationHelper.validateIfPostExists(PostId.create(postUlid));

        return queryRepository.findByPost(PostId.create(postUlid), Author.createWithNullableUuid(authorId))
                .stream().map(mapper::toCommentOfPostResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentPageResponse<CommentOfAuthorReadModel> gatherByAuthor(UUID authorId, Pageable pageable) {
        commentValidationHelper.validateIfAuthorExists(Author.create(authorId));

        Page<CommentOfAuthorReadModel> result = queryRepository.findByAuthor(Author.create(authorId), pageable);
        return mapper.toCommentPageResponseWithOnePlusPage(result);
    }

    public void register(CommentRegisterRequest request, UUID authorId) {
        PostId postId = PostId.create(request.postId());
        CommentPath path = CommentPath.create(request.path());
        Author author = Author.create(authorId);
        commentValidationHelper.validateIfPostExists(postId);
        commentValidationHelper.validateIfPostIsPublished(postId);
        validateIfParentCommentExists(postId, path);

        // 멱등성 검증 후, 같은 부모 아래의 다음 형제 경로 순서 값을 Redis에서 예약받는다.
        // DB 하한값 조회(findMaximumSiblingPathOrder)는 Redis 예약 키가 전혀 없을 때만 실행되도록 지연 전달한다.
        Optional<CommentPath> optionalReservedPath =
                cacheRepository.reservePath(postId, path, author,
                        () -> queryRepository.findMaximumSiblingPathOrder(postId, path));
        if (optionalReservedPath.isEmpty()) {
            return;
        }
        CommentPath reservedPath = optionalReservedPath.get();

        Comment comment = Comment.create(
                postId, reservedPath, author, CommentContent.create(swearService.filterSwear(request.content())));
        commandRepository.save(comment);

        applicationEventPublisher.publishEvent(
                CommentRegisterEvent.create(authorId, request.postId(), reservedPath.getValue(), request.content())
        );
    }

    public void updateContent(CommentUpdateRequest request, UUID currentMemberUuid) {
        PostId postId = PostId.create(request.postId());
        CommentPath path = CommentPath.create(request.path());
        commentValidationHelper.validateIfPostExists(postId);
        commentValidationHelper.validateIfPostIsPublished(postId);
        commentValidationHelper.validateIfCommentExists(postId, path);
        commentValidationHelper.validateIfCommentWrittenByAuthor(postId, path, currentMemberUuid);

        commandRepository.updateContent(postId, path, CommentContent.create(request.content()));
    }

    public void delete(CommentDeleteRequest request, UUID currentMemberUuid) {
        PostId postId = PostId.create(request.postId());
        CommentPath path = CommentPath.create(request.path());
        commentValidationHelper.validateIfPostExists(postId);
        commentValidationHelper.validateIfPostIsPublished(postId);
        commentValidationHelper.validateIfCommentExists(postId, path);
        commentValidationHelper.validateIfCommentWrittenByAuthor(postId, path, currentMemberUuid);

        commandRepository.setCommentAsDeleted(postId, path);
    }

    private void validateIfParentCommentExists(PostId postId, CommentPath path) {
        String pathValue = path.getValue();
        if (!pathValue.contains(".")) {
            return;
        }
        String parentCommentPath = pathValue.substring(0, pathValue.lastIndexOf("."));
        if (!queryRepository.isCommentExists(postId, CommentPath.create(parentCommentPath))) {
            throw new InvalidValueException(CommentErrorCode.NOT_EXIST_PARENT_COMMENT, "commentPath");
        }
    }
}
