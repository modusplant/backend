package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityRoleJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "role", expression = "java( kr.modusplant.infrastructure.security.enums.Role.USER )")
    SiteMemberRoleEntity toSiteMemberRoleEntity(SiteMemberEntity savedMember);
}
