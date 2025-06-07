package kr.modusplant.domains.communication.tip.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.MEMBER_UUID;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.FieldName.AUTH_MEMBER;
import static kr.modusplant.global.vo.FieldName.NICKNAME;

@Mapper
public interface TipPostAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = CATEGORY, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = CATEGORY, target = "categoryUuid", qualifiedByName = "toCategoryUuid")
    @Mapping(source = CATEGORY, target = "categoryOrder", qualifiedByName = "toCategoryOrder")
    @Mapping(source = AUTH_MEMBER, target = MEMBER_UUID, qualifiedByName = "toMemberUuid")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    TipPostResponse toTipPostResponse(TipPostEntity tipPostEntity);

    @Named("toCategory")
    default String toCategory(TipCategoryEntity tipCategoryEntity) {
        return tipCategoryEntity.getCategory();
    }

    @Named("toCategoryUuid")
    default UUID toCategoryUuid(TipCategoryEntity tipCategoryEntity) {
        return tipCategoryEntity.getUuid();
    }

    @Named("toCategoryOrder")
    default Integer toCategoryOrder(TipCategoryEntity tipCategoryEntity) {
        return tipCategoryEntity.getOrder();
    }
}
