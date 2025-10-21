package kr.modusplant.domains.member.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;

public interface MemberJpaMapper {
    SiteMemberEntity toMemberEntity(MemberNickname memberNickname);

    SiteMemberEntity toMemberEntity(MemberId memberId, MemberNickname memberNickname);

    Member toMember(SiteMemberEntity entity);
}
