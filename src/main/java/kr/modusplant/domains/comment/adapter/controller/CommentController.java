package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.in.web.cache.CommentCacheService;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.framework.out.persistence.jooq.CommentJooqRepository;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentRepositoryJpaAdapter;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentJooqRepository jooqRepository;
    private final CommentRepositoryJpaAdapter jpaAdapter;
    private final CommPostJpaRepository postJpaRepository;
    private final SiteMemberJpaRepository memberJpaRepository;
    private final SwearService swearService;

    private final CommentCacheService cacheService;

    public CommentCacheData getCacheData(String postUlid, String ifNoneMatch, String ifModifiedSince) {
        return cacheService.getCacheData(ifNoneMatch, ifModifiedSince, PostId.create(postUlid));

    }

    public CommentCacheData getCacheData(UUID memberUuid, String ifNoneMatch, String ifModifiedSince) {
        return cacheService.getCacheData(ifNoneMatch, ifModifiedSince, MemberId.fromUuid(memberUuid));

    }

    public List<CommentOfPostResponse> gatherByPost(String postUlid) {
        if(!postJpaRepository.existsByUlid(postUlid)) {
            throw new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, "post");
        }

        return jooqRepository.findByPost(PostId.create(postUlid));
    }

    public CommentPageResponse<CommentOfAuthorPageModel> gatherByAuthor(UUID memberUuid, Pageable pageable) {
        if(!memberJpaRepository.existsById(memberUuid)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, "member");
        }
        PageImpl<CommentOfAuthorPageModel> result = jooqRepository.findByAuthor(Author.create(memberUuid), pageable);

        CommentPageResponse<CommentOfAuthorPageModel> response =
                new CommentPageResponse<>(result.getContent(), result.getNumber(),
                result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.hasNext(), result.hasPrevious());
        
        response.ApplyOneIndexBasedPage();

        return response;

    }

    public void register(CommentRegisterRequest request, UUID currentMemberUuid) {
        if(jooqRepository.existsByPostAndPath(PostId.create(request.postId()), CommentPath.create(request.path()))) {
            throw new InvalidValueException(CommentErrorCode.EXIST_COMMENT);
        }
        checkPathCondition(request.postId(), request.path());

        Comment comment = mapper.toComment(
                PostId.create(request.postId()),
                CommentPath.create(request.path()),
                Author.create(currentMemberUuid),
                CommentContent.create(swearService.filterSwear(request.content())));
        jpaAdapter.save(comment);
    }

    public void delete(String postUlid, String commentPath) {
        jpaAdapter.setCommentAsDeleted(CommCommentId.builder()
                .postUlid(postUlid)
                .path(commentPath)
                .build());
    }

    private void checkPathCondition(String postId, String path) {
        PostId commentPost = PostId.create(postId);

        // 댓글 경로가 1인 경우 게시글에 댓글이 없어야 등록 가능
        if(path.equals("1") && !(jooqRepository.countPostComment(commentPost) == 0)) {
            throw new InvalidValueException(CommentErrorCode.EXIST_POST_COMMENT);
        }

        int lastDotIndex = path.lastIndexOf(".");
        String lastNumOfPath = path.substring(lastDotIndex + 1);

        // 댓글 경로가 1로 끝나는 경우, 마지막 . 이후의 값을 제거한 경로에 해당하는 댓글이 있어야 댓글 등록 가능
        // 예시: 경로가 1.2.1인 댓글을 등록하려면 경로가 1.2인 댓글이 있어야 함
        String parentCommentPath = path.substring(0, lastDotIndex);
        if (lastNumOfPath.equals("1") && !jooqRepository.existsByPostAndPath(commentPost, CommentPath.create(parentCommentPath))) {
            throw new InvalidValueException(CommentErrorCode.NOT_EXIST_PARENT_COMMENT);
        }

        // 그 외의 경우 경로의 마지막 숫자 -1을 한 경로의 댓글이 있어야 댓글 등록 가능
        // 예시: 경로가 1.5.3인 댓글을 등록하려면 경로가 1.5.2인 댓글이 있어야 함
        String siblingPathLastNum = String.valueOf(Integer.parseInt(lastNumOfPath) - 1);
        String siblingCommentPath = path.substring(0, lastDotIndex + 1).concat(siblingPathLastNum);
        if (1 < Integer.parseInt(lastNumOfPath) && !jooqRepository.existsByPostAndPath(commentPost, CommentPath.create(siblingCommentPath))) {
            throw new InvalidValueException(CommentErrorCode.NOT_EXIST_SIBLING_COMMENT);
        }
    }

}
