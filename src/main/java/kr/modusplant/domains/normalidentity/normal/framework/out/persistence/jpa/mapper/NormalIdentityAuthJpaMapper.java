package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityAuthJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "originalMember", source = "savedMember")
    @Mapping(target = "activeMember", source = "savedMember")
    @Mapping(target = "email", source = "sign.credentials.email.email")
    @Mapping(target = "pw", source = "sign.credentials.password.password")
    @Mapping(target = "provider", expression = "java( kr.modusplant.legacy.domains.member.enums.AuthProvider.BASIC )")
    SiteMemberAuthEntity toSiteMemberAuthEntity(SiteMemberEntity savedMember, SignUpData sign);

}
