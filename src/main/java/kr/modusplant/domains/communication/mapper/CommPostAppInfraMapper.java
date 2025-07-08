package kr.modusplant.domains.communication.mapper;

import kr.modusplant.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.MEMBER_UUID;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.EntityFieldName.AUTH_MEMBER;
import static kr.modusplant.global.vo.EntityFieldName.NICKNAME;

@Mapper
public interface CommPostAppInfraMapper {

    @Mapping(source = CATEGORY, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = CATEGORY, target = "categoryUuid", qualifiedByName = "toCategoryUuid")
    @Mapping(source = CATEGORY, target = "categoryOrder", qualifiedByName = "toCategoryOrder")
    @Mapping(source = AUTH_MEMBER, target = MEMBER_UUID, qualifiedByName = "toMemberUuid")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    CommPostResponse toCommPostResponse(CommPostEntity commPostEntity);

    @Named("toCategory")
    default String toCategory(CommSecondaryCategoryEntity commSecondaryCategoryEntity) {
        return commSecondaryCategoryEntity.getCategory();
    }

    @Named("toCategoryUuid")
    default UUID toCategoryUuid(CommSecondaryCategoryEntity commSecondaryCategoryEntity) {
        return commSecondaryCategoryEntity.getUuid();
    }

    @Named("toCategoryOrder")
    default Integer toCategoryOrder(CommSecondaryCategoryEntity commSecondaryCategoryEntity) {
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
