package kr.modusplant.domains.member.common.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberStatus;

public interface MemberStatusTestUtils {
    MemberStatus testMemberActiveStatus = MemberStatus.active();
    MemberStatus testMemberInactiveStatus = MemberStatus.inactive();
}
