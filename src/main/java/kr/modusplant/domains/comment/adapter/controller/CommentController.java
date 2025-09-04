package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.adapter.model.MemberReadModel;
import kr.modusplant.domains.comment.adapter.presenter.CommentPresenter;
import kr.modusplant.domains.comment.adapter.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.adapter.repository.CommentRepository;
import kr.modusplant.domains.comment.adapter.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.adapter.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.adapter.response.CommentResponse;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentController {

    private final CommentMapperImpl mapper;
    private final CommentRepository repository;
    private final CommentAuthorRepository authorRepository;

    public List<CommentResponse> gatherByPost(String postUlid) {
        List<CommentReadModel> comments = repository.findByPost(PostId.create(postUlid));
        List<CommentResponse> responses = new ArrayList<>();

        for (CommentReadModel comment : comments){
            MemberReadModel member = authorRepository
                    .findByAuthor(Author.create(UUID.fromString(comment.authMemberUuid())));
            responses.add(CommentPresenter.toCommentResponse(comment, member));
        }

        return responses;
    }

    public List<CommentResponse> gatherByAuthor(UUID memberUuid) {
        List<CommentReadModel> comments = repository.findByAuthor(Author.create(memberUuid));
        List<CommentResponse> responses = new ArrayList<>();

        for (CommentReadModel comment : comments){
            MemberReadModel member = authorRepository
                    .findByAuthor(Author.create(UUID.fromString(comment.authMemberUuid())));
            responses.add(CommentPresenter.toCommentResponse(comment, member));
        }

        return responses;
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
