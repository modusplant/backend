package kr.modusplant.domains.account.shared.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    EMPTY_ACCOUNT_ID(HttpStatus.BAD_REQUEST.value(), "empty_account_id", "계정의 식별자가 비어 있습니다"),
    INVALID_ACCOUNT_ID(HttpStatus.BAD_REQUEST.value(), "invalid_account_id", "계정의 식별자가 유효하지 않습니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
