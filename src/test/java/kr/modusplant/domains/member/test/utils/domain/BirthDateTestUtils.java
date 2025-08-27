package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberBirthDate;

import static kr.modusplant.domains.member.test.constant.MemberLocalDateConstant.TEST_BIRTHDATE;

public interface BirthDateTestUtils {
    MemberBirthDate TEST_MEMBER_BIRTH_DATE = MemberBirthDate.of(TEST_BIRTHDATE);
}
