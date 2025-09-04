package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.adapter.model.MemberReadModel;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentAuthorMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "uuid", target = "memberUuid")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "isActive", target = "isActive")
    MemberReadModel toMemberReadModel(CommentMemberEntity entity);
}
