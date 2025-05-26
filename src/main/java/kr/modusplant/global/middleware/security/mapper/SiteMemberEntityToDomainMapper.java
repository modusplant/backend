package kr.modusplant.global.middleware.security.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.*;
import static kr.modusplant.global.vo.CamelCaseWord.ORIGINAL_MEMBER_UUID;

@Mapper
public interface SiteMemberEntityToDomainMapper {

    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "loggedInAt", ignore = true)
    @Mapping(target = "member", ignore = true)
    SiteMember toSiteMember(SiteMemberEntity memberEntity);
}
