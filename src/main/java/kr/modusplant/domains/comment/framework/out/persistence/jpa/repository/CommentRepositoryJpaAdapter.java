package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentWriteRepository;
import kr.modusplant.framework.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentWriteRepository {
    private final SiteMemberJpaRepository memberRepository;
    private final CommPostJpaRepository postRepository;
    private final CommentJpaRepository commentRepository;

    private final CommentJpaMapper mapper;

    @Override
    @Transactional
    public void save(Comment comment) {
        SiteMemberEntity commentAuthorEntity = memberRepository.findByUuid(comment.getAuthor().getMemberUuid())
                .orElseThrow(() -> new InvalidValueException(CommentErrorCode.NOT_EXIST_AUTHOR));
        CommPostEntity commentPostEntity = postRepository.findByUlid(comment.getPostId().getId())
                .orElseThrow(() -> new InvalidValueException(CommentErrorCode.NOT_EXIST_POST));
        CommCommentEntity commentEntity = mapper.toCommCommentEntity(comment, commentAuthorEntity, commentPostEntity);
        if(commentRepository.existsById(new CommCommentId(comment.getPostId().getId(), comment.getPath().getPath()))) {
            throw new InvalidValueException(CommentErrorCode.EXIST_COMMENT);
        }
        commentRepository.save(commentEntity);
    }

    @Override
    public void update(CommCommentId id, CommentContent content) {
        Optional<CommCommentEntity> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            CommCommentEntity actualComment = comment.get();
            actualComment.updateContent(content.getContent());
            commentRepository.save(actualComment);
        } else {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT);
        }
    }

    @Override
    @Transactional
    public void setCommentAsDeleted(CommCommentId id) {
        Optional<CommCommentEntity> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            CommCommentEntity actualComment = comment.get();
            actualComment.markAsDeleted();
            commentRepository.save(actualComment);
        }
    }

}
