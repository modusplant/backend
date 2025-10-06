package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberStatus;

public interface MemberStatusTestUtils {
    MemberStatus testMemberActiveStatus = MemberStatus.active();
    MemberStatus testMemberInactiveStatus = MemberStatus.inactive();
}
