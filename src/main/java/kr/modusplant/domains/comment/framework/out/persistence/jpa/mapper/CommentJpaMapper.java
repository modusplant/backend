package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentStatus;
import kr.modusplant.domains.comment.domain.vo.enums.CommentStatusType;
import kr.modusplant.framework.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "comment.path.path", target = "path")
    @Mapping(source = "commentPost", target = "postEntity")
    @Mapping(source = "commentAuthor", target = "authMember")
    @Mapping(source = "comment.content.content", target = "content")
    @Mapping(source = "comment.status", target = "isDeleted", qualifiedByName = "mapIsDeleted")
    CommCommentEntity toCommCommentEntity(Comment comment, SiteMemberEntity commentAuthor, CommPostEntity commentPost);

    @Named("mapIsDeleted")
    default boolean mapIsDeleted(CommentStatus status) {
        CommentStatusType type = status.getStatus();

        return switch (type) {
            case CommentStatusType.VALID -> false;
            case CommentStatusType.DELETED -> true;
        };
    }

}
