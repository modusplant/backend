package kr.modusplant.domains.member.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResponseCode {
    EMPTY_MEMBER_BIRTH_DATE(HttpStatus.BAD_REQUEST, "empty_member_birth_date", "회원 생일이 비어 있습니다. "),
    EMPTY_MEMBER_ID(HttpStatus.BAD_REQUEST, "empty_member_id", "회원 아이디가 비어 있습니다. "),
    EMPTY_MEMBER_NICKNAME(HttpStatus.BAD_REQUEST, "empty_member_nickname", "회원 닉네임이 비어 있습니다. "),
    EMPTY_MEMBER_STATUS(HttpStatus.BAD_REQUEST, "empty_member_status", "회원 상태가 비어 있습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
