package kr.modusplant.domains.member.common.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberBirthDate;

import static kr.modusplant.domains.member.common.constant.MemberLocalDateConstant.TEST_MEMBER_BIRTHDATE;

public interface BirthDateTestUtils {
    MemberBirthDate TEST_MEMBER_BIRTH_DATE = MemberBirthDate.create(TEST_MEMBER_BIRTHDATE);
}
