package kr.modusplant.global.middleware.security.mapper;

import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SiteMemberRoleEntityToDomainMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "memberRole", ignore = true)
    SiteMemberRole toSiteMemberRole(SiteMemberRoleEntity memberRoleEntity);
}
