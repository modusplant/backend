package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.enums.AuthProvider;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityAuthJpaMapper {

    public MemberAuthEntity toSiteMemberAuthEntity(MemberEntity savedMember, SignUpData sign, String encodedPassword) {
        return MemberAuthEntity.builder()
                .member(savedMember)
                .email(sign.getNormalCredentials().getEmail().getValue())
                .pw(encodedPassword)
                .provider(AuthProvider.BASIC)
                .build();
    }
}
