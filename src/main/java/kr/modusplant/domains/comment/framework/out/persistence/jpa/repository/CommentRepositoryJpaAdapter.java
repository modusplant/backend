package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentWriteRepository;
import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentWriteRepository {
    private final SiteMemberJpaRepository memberRepository;
    private final CommentJpaRepository commentRepository;
    private final CommentJpaMapper mapper;

    @Override
    public void save(Comment comment) {
        SiteMemberEntity commentAuthorEntity = memberRepository.findByUuid(comment.getAuthor().getMemberUuid())
                .orElseThrow(() -> new InvalidValueException(CommentErrorCode.NOT_EXIST_AUTHOR));
        CommCommentEntity commentEntity = mapper.toCommCommentEntity(comment, commentAuthorEntity);
        commentRepository.save(commentEntity);
    }

    @Override
    public void deleteById(CommCommentId id) { commentRepository.deleteById(id); }

}
