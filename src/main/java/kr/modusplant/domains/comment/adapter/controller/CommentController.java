package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.usecase.port.repository.CommentRepository;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentRepository repository;

    public List<CommentResponse> gatherByPost(String postUlid) {
        return repository.findByPost(PostId.create(postUlid));
    }

    public List<CommentResponse> gatherByAuthor(UUID memberUuid) {
        return repository.findByAuthor(Author.create(memberUuid));
    }

    public void register(CommentRegisterRequest request) {
        Comment comment = mapper.toComment(request);
        repository.save(comment);
    }

    public void delete(CommentDeleteRequest request) {
        repository.deleteById(CommentCompositeKey.builder()
                .postUlid(request.postUlid())
                .path(request.path())
                .build());
    }

}
