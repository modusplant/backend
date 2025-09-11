package kr.modusplant.domains.member.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;

public interface MemberJpaMapper {
    MemberEntity toMemberEntity(Member member);

    Member toMember(MemberEntity entity);
}
