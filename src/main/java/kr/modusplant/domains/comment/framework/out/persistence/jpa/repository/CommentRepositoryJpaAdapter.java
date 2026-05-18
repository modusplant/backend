package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.usecase.port.repository.CommentWriteRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.compositekey.CommentCompositeKey;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentWriteRepository {
    private final MemberJpaRepository memberRepository;
    private final PostJpaRepository postRepository;
    private final CommentJpaRepository commentRepository;

    private final CommentJpaMapper mapper;

    @Override
    public void save(Comment comment) {
        MemberEntity commentAuthorEntity = memberRepository.findByUuid(comment.getAuthor().getMemberUuid())
                .orElseThrow(() -> new InvalidValueException(CommentErrorCode.NOT_EXIST_AUTHOR));
        PostEntity commentPostEntity = postRepository.findByUlid(comment.getPostId().getId())
                .orElseThrow(() -> new InvalidValueException(CommentErrorCode.NOT_EXIST_POST));
        CommentEntity commentEntity = mapper.toCommCommentEntity(comment, commentAuthorEntity, commentPostEntity);
        if(commentRepository.existsById(new CommentCompositeKey(comment.getPostId().getId(), comment.getPath().getPath()))) {
            throw new InvalidValueException(CommentErrorCode.EXIST_COMMENT);
        }
        commentRepository.save(commentEntity);
    }

    @Override
    public void update(CommentCompositeKey id, CommentContent content) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, TableName.COMM_COMMENT));
        comment.updateContent(content.getContent());
        comment.updateEditedAt();
        
        commentRepository.save(comment);
    }

    @Override
    public void setCommentAsDeleted(CommentCompositeKey id) {
        Optional<CommentEntity> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            CommentEntity actualComment = comment.get();
            actualComment.markAsDeleted();
            commentRepository.save(actualComment);
        }
    }

}
