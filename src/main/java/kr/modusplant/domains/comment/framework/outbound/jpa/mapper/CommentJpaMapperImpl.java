package kr.modusplant.domains.comment.framework.outbound.jpa.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentStatus;
import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.jpa.mapper.supers.CommentJpaMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentJpaMapperImpl implements CommentJpaMapper {
    @Override
    public CommentEntity toCommCommentEntity(Comment comment, MemberEntity commentAuthor, PostEntity parentPost) {
        return CommentEntity.builder()
                .post(parentPost)
                .path(comment.getPath().getValue())
                .replyToPath(comment.getReplyToPath().getValue())
                .authMember(commentAuthor)
                .content(comment.getContent().getValue())
                .isDeleted(mapIsDeleted(comment.getStatus()))
                .build();
    }

    private boolean mapIsDeleted(CommentStatus status) {
        return switch (status.getValue()) {
            case ACTIVE -> false;
            case DELETED -> true;
        };
    }
}
