package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityTermJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "agreedTermsOfUseVersion", source = "sign.agreedTermsOfUseVersion.value")
    @Mapping(target = "agreedPrivacyPolicyVersion", source = "sign.agreedPrivacyPolicyVersion.value")
    @Mapping(target = "agreedCommunityPolicyVersion", source = "sign.agreedCommunityPolicyVersion.value")
    MemberTermEntity toSiteMemberTermEntity(MemberEntity savedMember, SignUpData sign);

}
