package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IdentityTermJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "agreedTermsOfUseVersion", source = "sign.agreedTermsOfUseVersion.version")
    @Mapping(target = "agreedPrivacyPolicyVersion", source = "sign.agreedPrivacyPolicyVersion.version")
    @Mapping(target = "agreedAdInfoReceivingVersion", source = "sign.agreedAdInfoReceivingVersion.version")
    SiteMemberTermEntity toSiteMemberTermEntity(SiteMemberEntity savedMember, SignUpData sign);

}
