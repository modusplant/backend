package kr.modusplant.domains.member.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_BIRTH_DATE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberLocalDateConstant {
    public static final LocalDate TEST_MEMBER_BIRTHDATE = MEMBER_BASIC_USER_BIRTH_DATE;
}
