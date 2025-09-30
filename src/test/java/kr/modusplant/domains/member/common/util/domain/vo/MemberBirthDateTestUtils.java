package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberBirthDate;

import static kr.modusplant.domains.member.common.constant.MemberLocalDateConstant.TEST_MEMBER_BIRTHDATE;

public interface MemberBirthDateTestUtils {
    MemberBirthDate testMemberBirthDate = MemberBirthDate.create(TEST_MEMBER_BIRTHDATE);
}
