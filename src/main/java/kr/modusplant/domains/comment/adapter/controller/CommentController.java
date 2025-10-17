package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jooq.CommentRepositoryJooqAdapter;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentRepositoryJpaAdapter;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentRepositoryJooqAdapter jooqAdapter;
    private final CommentRepositoryJpaAdapter jpaAdapter;

    public List<CommentResponse> gatherByPost(String postUlid) {
        return jooqAdapter.findByPost(PostId.create(postUlid));
    }

    public List<CommentResponse> gatherByAuthor(UUID memberUuid) {
        return jooqAdapter.findByAuthor(Author.create(memberUuid));
    }

    public void register(CommentRegisterRequest request) {
        Comment comment = mapper.toComment(request);
        jpaAdapter.save(comment);
    }

    public void delete(CommentDeleteRequest request) {
        jpaAdapter.deleteById(CommCommentId.builder()
                .postUlid(request.postUlid())
                .path(request.path())
                .build());
    }

}
