package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.adapter.repository.CommentRepository;
import kr.modusplant.domains.comment.adapter.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.adapter.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentRepository repository;

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
