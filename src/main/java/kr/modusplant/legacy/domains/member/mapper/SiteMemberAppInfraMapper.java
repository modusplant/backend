package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static kr.modusplant.framework.out.persistence.constant.EntityFieldName.*;

@Mapper
public interface SiteMemberAppInfraMapper {
    @Mapping(target = "memberEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = BIRTH_DATE, ignore = true)
    @Mapping(target = IS_ACTIVE, ignore = true)
    @Mapping(target = IS_DISABLED_BY_LINKING, ignore = true)
    @Mapping(target = IS_BANNED, ignore = true)
    @Mapping(target = IS_DELETED, ignore = true)
    @Mapping(target = LOGGED_IN_AT, ignore = true)
    SiteMemberEntity toMemberEntity(SiteMemberInsertRequest memberInsertRequest);

    SiteMemberResponse toMemberResponse(SiteMemberEntity memberEntity);
}
