package kr.modusplant.legacy.domains.communication.mapper;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.communication.app.http.response.CommPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.AUTH_MEMBER;
import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.NICKNAME;
import static kr.modusplant.legacy.domains.communication.constant.CommCategoryWord.PRIMARY_CATEGORY;
import static kr.modusplant.legacy.domains.communication.constant.CommCategoryWord.SECONDARY_CATEGORY;
import static kr.modusplant.legacy.domains.member.vo.MemberUuid.MEMBER_UUID;

@Mapper
public interface CommPostAppInfraMapper {

    @Mapping(source = PRIMARY_CATEGORY, target = PRIMARY_CATEGORY, qualifiedByName = "toPrimaryCategory")
    @Mapping(source = PRIMARY_CATEGORY, target = "primaryCategoryUuid", qualifiedByName = "toPrimaryCategoryUuid")
    @Mapping(source = PRIMARY_CATEGORY, target = "primaryCategoryOrder", qualifiedByName = "toPrimaryCategoryOrder")
    @Mapping(source = SECONDARY_CATEGORY, target = SECONDARY_CATEGORY, qualifiedByName = "toSecondaryCategory")
    @Mapping(source = SECONDARY_CATEGORY, target = "secondaryCategoryUuid", qualifiedByName = "toSecondaryCategoryUuid")
    @Mapping(source = SECONDARY_CATEGORY, target = "secondaryCategoryOrder", qualifiedByName = "toSecondaryCategoryOrder")
    @Mapping(source = AUTH_MEMBER, target = MEMBER_UUID, qualifiedByName = "toMemberUuid")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    CommPostResponse toCommPostResponse(CommPostEntity commPostEntity);

    @Named("toPrimaryCategory")
    default String toPrimaryCategory(CommPrimaryCategoryEntity commPrimaryCategoryEntity) {
        return commPrimaryCategoryEntity.getCategory();
    }

    @Named("toPrimaryCategoryUuid")
    default UUID toPrimaryCategoryUuid(CommPrimaryCategoryEntity commPrimaryCategoryEntity) {
        return commPrimaryCategoryEntity.getUuid();
    }

    @Named("toPrimaryCategoryOrder")
    default Integer toPrimaryCategoryOrder(CommPrimaryCategoryEntity commPrimaryCategoryEntity) {
        return commPrimaryCategoryEntity.getOrder();
    }

    @Named("toSecondaryCategory")
    default String toSecondaryCategory(CommSecondaryCategoryEntity commSecondaryCategoryEntity) {
        return commSecondaryCategoryEntity.getCategory();
    }

    @Named("toSecondaryCategoryUuid")
    default UUID toSecondaryCategoryUuid(CommSecondaryCategoryEntity commSecondaryCategoryEntity) {
        return commSecondaryCategoryEntity.getUuid();
    }

    @Named("toSecondaryCategoryOrder")
    default Integer toSecondaryCategoryOrder(CommSecondaryCategoryEntity commSecondaryCategoryEntity) {
        return commSecondaryCategoryEntity.getOrder();
    }

    @Named("toMemberUuid")
    default UUID toMemberUuid(SiteMemberEntity member) {
        return member.getUuid();
    }

    @Named("toNickname")
    default String toNickname(SiteMemberEntity siteMemberEntity) {
        return siteMemberEntity.getNickname();
    }
}
