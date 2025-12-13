package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityTermJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "agreedTermsOfUseVersion", source = "sign.agreedTermsOfUseVersion.value")
    @Mapping(target = "agreedPrivacyPolicyVersion", source = "sign.agreedPrivacyPolicyVersion.value")
    @Mapping(target = "agreedAdInfoReceivingVersion", source = "sign.agreedAdInfoReceivingVersion.value")
    SiteMemberTermEntity toSiteMemberTermEntity(SiteMemberEntity savedMember, SignUpData sign);

}
