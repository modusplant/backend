package kr.modusplant.modules.auth.normal.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;
import static kr.modusplant.global.vo.EntityFieldName.*;

@Mapper
public interface NormalSignUpMemberAppDomainMapper {

    @Mapping(target = MEMBER, ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = BIRTH_DATE, ignore = true)
    @Mapping(target = IS_ACTIVE, ignore = true)
    @Mapping(target = IS_DISABLED_BY_LINKING, ignore = true)
    @Mapping(target = IS_BANNED, ignore = true)
    @Mapping(target = IS_DELETED, ignore = true)
    @Mapping(target = LOGGED_IN_AT, ignore = true)
    SiteMember toSiteMember(NormalSignUpRequest request);

    SiteMemberInsertRequest toSiteMemberInsertRequest(NormalSignUpRequest request);

}