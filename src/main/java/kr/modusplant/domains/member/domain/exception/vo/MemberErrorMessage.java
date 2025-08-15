package kr.modusplant.domains.member.domain.exception.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberErrorMessage {
    public static final String EMPTY_PASSWORD = "닉네임이 비어 있습니다. ";
}
