package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.PostEntity;
import kr.modusplant.domains.member.framework.out.persistence.jpa.entity.MemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

//@Component
@Mapper(componentModel = "spring")
public interface CommentJpaMapper {

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
    default MemberEntity mapCommentMember(Author author) {
        return MemberEntity.builder()
                .uuid(author.getMemberUuid())
                .build();
    }

}
