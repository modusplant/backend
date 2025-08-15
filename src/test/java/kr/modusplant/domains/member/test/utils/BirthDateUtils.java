package kr.modusplant.domains.member.test.utils;

import kr.modusplant.domains.member.domain.vo.BirthDate;

import static kr.modusplant.domains.member.test.vo.MemberLocalDateVO.TEST_BIRTHDATE;

public interface BirthDateUtils {
    BirthDate testBirthDate = BirthDate.of(TEST_BIRTHDATE);
}
