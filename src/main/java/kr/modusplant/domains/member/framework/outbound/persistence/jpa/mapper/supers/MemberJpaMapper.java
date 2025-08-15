package kr.modusplant.domains.member.framework.outbound.persistence.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.entity.MemberEntity;

public interface MemberJpaMapper {
    MemberEntity toMemberEntity(Member member);

    Member toMember(MemberEntity entity);
}
