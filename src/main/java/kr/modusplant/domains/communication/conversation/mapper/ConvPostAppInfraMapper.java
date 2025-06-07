package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.MEMBER_UUID;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.FieldName.AUTH_MEMBER;
import static kr.modusplant.global.vo.FieldName.NICKNAME;

@Mapper
public interface ConvPostAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = CATEGORY, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = CATEGORY, target = "categoryUuid", qualifiedByName = "toCategoryUuid")
    @Mapping(source = CATEGORY, target = "categoryOrder", qualifiedByName = "toCategoryOrder")
    @Mapping(source = AUTH_MEMBER, target = MEMBER_UUID, qualifiedByName = "toMemberUuid")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    ConvPostResponse toConvPostResponse(ConvPostEntity convPostEntity);

    @Named("toCategory")
    default String toCategory(ConvCategoryEntity convCategoryEntity) {
        return convCategoryEntity.getCategory();
    }

    @Named("toCategoryUuid")
    default UUID toCategoryUuid(ConvCategoryEntity convCategoryEntity) {
        return convCategoryEntity.getUuid();
    }

    @Named("toCategoryOrder")
    default Integer toCategoryOrder(ConvCategoryEntity convCategoryEntity) {
        return convCategoryEntity.getOrder();
    }
}
