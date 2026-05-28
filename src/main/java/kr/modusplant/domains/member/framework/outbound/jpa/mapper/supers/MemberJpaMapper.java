package kr.modusplant.domains.member.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.kernel.Nickname;

public interface MemberJpaMapper {
    MemberEntity toMemberEntity(Nickname nickname);

    MemberEntity toMemberEntity(MemberId memberId, Nickname nickname);

    Member toMember(MemberEntity entity);
}
