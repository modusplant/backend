package kr.modusplant.domains.comment.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;

public interface CommentJpaMapper {
    CommentEntity toCommCommentEntity(Comment comment, MemberEntity commentAuthor, PostEntity immediateParentPost);
}
