package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.kernel.Nickname;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityJpaMapper {

    public MemberEntity toMemberEntity(Nickname nickname) {
        return MemberEntity.builder()
                .nickname(nickname.getValue())
                .build();
    }
}
