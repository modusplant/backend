package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.account.identity.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityAuthJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "email", source = "sign.normalCredentials.email.value")
    @Mapping(target = "pw", source = "sign.normalCredentials.password.value")
    @Mapping(target = "provider", expression = "java( kr.modusplant.shared.enums.AuthProvider.BASIC )")
    MemberAuthEntity toSiteMemberAuthEntity(MemberEntity savedMember, SignUpData sign);

}
