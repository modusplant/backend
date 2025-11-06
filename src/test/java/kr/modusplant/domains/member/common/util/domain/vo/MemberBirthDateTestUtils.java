package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberBirthDate;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_BIRTH_DATE;

public interface MemberBirthDateTestUtils {
    MemberBirthDate testMemberBirthDate = MemberBirthDate.create(MEMBER_BASIC_USER_BIRTH_DATE);
}
