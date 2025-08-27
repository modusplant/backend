package kr.modusplant.domains.member.domain.exception.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberErrorMessage {
    public static final String EMPTY_MEMBER_ID = "회원 아이디가 비어 있습니다. ";
    public static final String EMPTY_MEMBER_NICKNAME = "회원 닉네임이 비어 있습니다. ";
    public static final String EMPTY_MEMBER_STATUS = "회원 상태가 비어 있습니다. ";
}
