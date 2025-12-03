package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jooq.CommentJooqRepository;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentRepositoryJpaAdapter;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
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

    public List<CommentOfPostResponse> gatherByPost(String postUlid) {
        return jooqRepository.findByPost(PostId.create(postUlid));
    }

    public CommentPageResponse<CommentOfAuthorPageModel> gatherByAuthor(UUID memberUuid, Pageable pageable) {
        PageImpl<CommentOfAuthorPageModel> result = jooqRepository.findByAuthor(Author.create(memberUuid), pageable);

        return new CommentPageResponse<>(result.getContent(), result.getNumber(),
                result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.hasNext(), result.hasPrevious());
    }

    public void register(CommentRegisterRequest request) {
        Comment comment = mapper.toComment(request);
        jpaAdapter.save(comment);
    }

    public void delete(String postUlid, String commentPath) {
        jpaAdapter.setCommentAsDeleted(CommCommentId.builder()
                .postUlid(postUlid)
                .path(commentPath)
                .build());
    }

}
