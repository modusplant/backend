package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentMemberEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.PostEntity;
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
    CommentEntity toCommentEntity(Comment comment);

    @Named("mapCommentId")
    default CommentCompositeKey mapCommentId(Comment comment) {
        return CommentCompositeKey.builder()
                .postUlid(comment.getPostId().getId())
                .path(comment.getPath().getPath())
                .build();
    }

    @Named("mapPostEntity")
    default PostEntity mapPostEntity(PostId postId) {
        return PostEntity.create(postId.getId());
    }

    @Named("mapMember")
    default CommentMemberEntity mapCommentMember(Author author) {
        return CommentMemberEntity.builder()
                .uuid(author.getMemberUuid())
                .build();
    }

}
