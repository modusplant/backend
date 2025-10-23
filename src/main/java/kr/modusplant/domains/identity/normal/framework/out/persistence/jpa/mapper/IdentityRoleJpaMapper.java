package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface IdentityRoleJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "role", expression = "java( kr.modusplant.infrastructure.security.enums.Role.USER )")
    SiteMemberRoleEntity toSiteMemberRoleEntity(SiteMemberEntity savedMember);

    // TODO: Remove if mapping is successful
    @Named("mapMember")
    default SiteMemberEntity mapMember(UUID memberUuid) {
        return SiteMemberEntity.builder()
                .uuid(memberUuid)
                .build();
    }
}
