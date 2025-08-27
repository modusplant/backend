package kr.modusplant.domains.member.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static kr.modusplant.domains.member.domain.exception.constant.MemberErrorMessage.*;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResponseCode {
    EMPTY_ID(HttpStatus.BAD_REQUEST, "empty_member_id", EMPTY_MEMBER_ID),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "empty_member_nickname", EMPTY_MEMBER_NICKNAME),
    EMPTY_STATUS(HttpStatus.BAD_REQUEST, "empty_member_status", EMPTY_MEMBER_STATUS);

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
