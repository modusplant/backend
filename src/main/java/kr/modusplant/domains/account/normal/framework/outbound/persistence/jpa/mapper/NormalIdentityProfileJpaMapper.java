package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityProfileJpaMapper {

    public MemberProfileEntity toSiteMemberProfileEntity(MemberEntity savedMember) {
        return MemberProfileEntity.builder()
                .member(savedMember)
                .build();
    }
}
