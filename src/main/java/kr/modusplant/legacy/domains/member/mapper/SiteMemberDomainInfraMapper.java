package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.legacy.domains.member.domain.model.SiteMember;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SiteMemberDomainInfraMapper {

    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "loggedInAt", ignore = true)
    @Mapping(target = "member", ignore = true)
    SiteMember toSiteMember(SiteMemberEntity memberEntity);
}
