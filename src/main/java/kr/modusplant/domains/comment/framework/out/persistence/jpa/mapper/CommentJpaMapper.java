package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.enums.CommentStatusType;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentStatus;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id.postUlid", target = "postUlid")
    @Mapping(source = "id.path", target = "path")
    @Mapping(source = "authMember.uuid", target = "authMemberUuid")
    @Mapping(source = "createMember.uuid", target = "createMemberUuid")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "isDeleted", target = "isDeleted")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentReadModel toCommentReadModel(CommentEntity commentEntity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = ".", target = "id", qualifiedByName = "mapCommentId")
    @Mapping(source = "postId", target = "postEntity", qualifiedByName = "mapPostEntity")
    @Mapping(source = "author", target = "authMember", qualifiedByName = "mapMember")
    @Mapping(source = "author", target = "createMember", qualifiedByName = "mapMember")
    @Mapping(source = "content.content", target = "content")
    @Mapping(source = "status", target = "isDeleted", qualifiedByName = "mapIsDeleted")
    CommentEntity toCommentEntity(Comment comment);

    @Named("mapCommentId")
    default CommentCompositeKey mapCommentId(Comment comment) {
        return CommentCompositeKey.builder()
                .postUlid(comment.getPostId().getId())
                .path(comment.getPath().getPath())
                .build();
    }

    @Named("mapPostEntity")
    default CommPostEntity mapPostEntity(PostId postId) {
        return CommPostEntity.builder().ulid(postId.getId()).build();
    }

    @Named("mapMember")
    default SiteMemberEntity mapCommentMember(Author author) {
        return SiteMemberEntity.builder()
                .uuid(author.getMemberUuid())
                .build();
    }

    @Named("mapIsDeleted")
    default boolean mapIsDeleted(CommentStatus status) {
        CommentStatusType type = status.getStatus();

        return switch (type) {
            case CommentStatusType.VALID -> false;
            case CommentStatusType.DELETED -> true;
        };
    }

}
