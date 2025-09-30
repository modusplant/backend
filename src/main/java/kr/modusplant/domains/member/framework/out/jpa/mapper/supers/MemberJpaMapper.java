package kr.modusplant.domains.member.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;

public interface MemberJpaMapper {
    SiteMemberEntity toMemberEntity(Member member);

    Member toMember(SiteMemberEntity entity);
}
