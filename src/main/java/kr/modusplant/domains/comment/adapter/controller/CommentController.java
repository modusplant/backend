package kr.modusplant.domains.comment.adapter.controller;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.in.web.cache.CommentCacheService;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.port.outbound.CommentPostValidator;
import kr.modusplant.domains.comment.usecase.port.repository.CommentReadRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentWriteRepository;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentReadRepository readRepository;
    private final CommentWriteRepository writeRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final SiteMemberJpaRepository memberJpaRepository;
    private final SwearService swearService;

    private final CommentPostValidator postValidator;
    private final CommentCacheService cacheService;

    @Transactional
    public CommentCacheData getCacheData(String postUlid, String ifNoneMatch, String ifModifiedSince) {
        return cacheService.getCacheData(ifNoneMatch, ifModifiedSince, PostId.create(postUlid));
    }

    @Transactional
    public CommentCacheData getCacheData(UUID memberUuid, String ifNoneMatch, String ifModifiedSince) {
        return cacheService.getCacheData(ifNoneMatch, ifModifiedSince, MemberId.fromUuid(memberUuid));
    }

    @Transactional
    public List<CommentOfPostResponse> gatherByPost(String postUlid, UUID currentMemberUuid) {
        if(!postJpaRepository.existsByUlid(postUlid)) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post");
        }

        return readRepository.findByPost(PostId.create(postUlid), Author.createNullable(currentMemberUuid))
                .stream().map(mapper::toCommentOfPostResponse)
                .toList();
    }

    @Transactional
    public CommentPageResponse<CommentOfAuthorPageModel> gatherByAuthor(UUID memberUuid, Pageable pageable) {
        if(!memberJpaRepository.existsById(memberUuid)) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, "member");
        }
        PageImpl<CommentOfAuthorPageModel> result = readRepository.findByAuthor(Author.create(memberUuid), pageable);

        CommentPageResponse<CommentOfAuthorPageModel> response =
                new CommentPageResponse<>(result.getContent(), result.getNumber(),
                result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.hasNext(), result.hasPrevious());
        
        response.applyOneIndexBasedPage();

        return response;
    }

    @Transactional
    public void register(CommentRegisterRequest request, UUID currentMemberUuid) {
        if(readRepository.existsByPostAndPath(PostId.create(request.postId()), CommentPath.create(request.path()))) {
            throw new InvalidValueException(CommentErrorCode.EXIST_COMMENT, "comment");
        }

        if (!postValidator.isPostPublished(request.postId())) {
            throw new InvalidValueException(CommentErrorCode.NOT_PUBLISHED_POST, "comment");
        }

        checkPathCondition(request.postId(), request.path());

        Comment comment = mapper.toComment(
                PostId.create(request.postId()),
                CommentPath.create(request.path()),
                Author.create(currentMemberUuid),
                CommentContent.create(swearService.filterSwear(request.content())));
        writeRepository.save(comment);
    }

    @Transactional
    public void update(CommentUpdateRequest request) {
        if(!readRepository.existsByPostAndPath(PostId.create(request.postId()), CommentPath.create(request.path()))) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT);
        }

        CommCommentId id = CommCommentId.builder()
                .post(request.postId())
                .path(request.path())
                .build();

        writeRepository.update(id, CommentContent.create(request.content()));
    }

    @Transactional
    public void delete(String postUlid, String commentPath) {
        writeRepository.setCommentAsDeleted(CommCommentId.builder()
                .post(postUlid)
                .path(commentPath)
                .build());
    }

    private void checkPathCondition(String postId, String path) {
        PostId commentPost = PostId.create(postId);

        if (path.contains(".")) {
            int lastDotIndex = path.lastIndexOf(".");
            String lastNumOfPath = path.substring(lastDotIndex + 1);

            // 댓글 경로가 1로 끝나는 경우, 마지막 . 이후의 값을 제거한 경로에 해당하는 댓글이 있어야 댓글 등록 가능
            // 예시: 경로가 1.2.1인 댓글을 등록하려면 경로가 1.2인 댓글이 있어야 함
            String parentCommentPath = path.substring(0, lastDotIndex);
            if (lastNumOfPath.equals("1") && !readRepository.existsByPostAndPath(commentPost, CommentPath.create(parentCommentPath))) {
                throw new InvalidValueException(CommentErrorCode.NOT_EXIST_PARENT_COMMENT);
            }

            // 그 외의 경우 경로의 마지막 숫자 -1을 한 경로의 댓글이 있어야 댓글 등록 가능
            // 예시: 경로가 1.5.3인 댓글을 등록하려면 경로가 1.5.2인 댓글이 있어야 함
            String siblingPathLastNum = String.valueOf(Integer.parseInt(lastNumOfPath) - 1);
            String siblingCommentPath = path.substring(0, lastDotIndex + 1).concat(siblingPathLastNum);
            if (1 < Integer.parseInt(lastNumOfPath) && !readRepository.existsByPostAndPath(commentPost, CommentPath.create(siblingCommentPath))) {
                throw new InvalidValueException(CommentErrorCode.NOT_EXIST_SIBLING_COMMENT);
            }
            
        } else {
            // 댓글 경로가 1인 경우 게시글에 댓글이 없어야 등록 가능
            if(path.equals("1")) {
                if (!(readRepository.countPostComment(commentPost) == 0)) {
                    throw new InvalidValueException(CommentErrorCode.EXIST_POST_COMMENT);
                }
            } else {
                // 댓글 경로에 .가 없고 1이 아닌 경우, 형제 댓글이 있어야 등록 가능
                String siblingCommentPath = String.valueOf(Integer.parseInt(path) - 1);
                if (!(readRepository.existsByPostAndPath(commentPost, CommentPath.create(siblingCommentPath)))) {
                    throw new InvalidValueException(CommentErrorCode.NOT_EXIST_SIBLING_COMMENT);
                }
            }
        }

    }
}
