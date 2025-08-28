package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.BirthDate;

import static kr.modusplant.domains.member.test.constant.MemberLocalDateConstant.TEST_BIRTHDATE;

public interface BirthDateTestUtils {
    BirthDate testBirthDate = BirthDate.of(TEST_BIRTHDATE);
}
