package kr.modusplant.domains.member.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberLocalDateConstant {
    public static final LocalDate TEST_MEMBER_BIRTHDATE = LocalDate.of(2000, 1, 1);
}
