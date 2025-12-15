package kr.modusplant.domains.member.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.kernel.Nickname;

public interface MemberJpaMapper {
    SiteMemberEntity toMemberEntity(Nickname nickname);

    SiteMemberEntity toMemberEntity(MemberId memberId, Nickname nickname);

    Member toMember(SiteMemberEntity entity);
}
