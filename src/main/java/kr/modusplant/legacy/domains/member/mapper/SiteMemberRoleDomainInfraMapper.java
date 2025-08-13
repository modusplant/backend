package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.legacy.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SiteMemberRoleDomainInfraMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "memberRole", ignore = true)
    SiteMemberRole toSiteMemberRole(SiteMemberRoleEntity memberRoleEntity);
}
