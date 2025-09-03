package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.adapter.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.adapter.repository.CommentRepository;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentRepository repository;

    public void register(CommentRegisterRequest registerRequest) {
        Comment comment = mapper.toComment(registerRequest);
        repository.save(comment);
//        return mapper.toResponse(repository.save(comment));
    }
}
