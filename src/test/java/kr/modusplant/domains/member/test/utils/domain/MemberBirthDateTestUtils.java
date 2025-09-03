package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberBirthDate;

import static kr.modusplant.domains.member.test.constant.MemberLocalDateConstant.TEST_MEMBER_BIRTHDATE;

public interface MemberBirthDateTestUtils {
    MemberBirthDate testMemberBirthDate = MemberBirthDate.create(TEST_MEMBER_BIRTHDATE);
}
