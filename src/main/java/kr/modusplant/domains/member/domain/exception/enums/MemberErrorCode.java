package kr.modusplant.domains.member.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static kr.modusplant.domains.member.domain.exception.vo.MemberErrorMessage.EMPTY_PASSWORD;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResponseCode {
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "empty_nickname", EMPTY_PASSWORD);

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
