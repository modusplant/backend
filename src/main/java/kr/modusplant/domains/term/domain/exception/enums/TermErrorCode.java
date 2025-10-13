package kr.modusplant.domains.term.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermErrorCode implements ResponseCode {
    EMPTY_TERM_ID(HttpStatus.BAD_REQUEST, "empty_term_id", "약관 아이디가 비어 있습니다. "),
    EMPTY_TERM_NAME(HttpStatus.BAD_REQUEST, "empty_term_name", "약관명이 비어 있습니다. "),
    EMPTY_TERM_CONTENT(HttpStatus.BAD_REQUEST, "empty_term_content", "약관내용이 비어 있습니다. "),
    EMPTY_TERM_VERSION(HttpStatus.BAD_REQUEST, "empty_term_version", "약관버전이 비어 있습니다. "),
    INVALID_TERM_VERSION(HttpStatus.BAD_REQUEST, "invalid_term_version", "약관버전 형식이 잘못되었습니다. (ex v1.0.1) "),

    NOT_FOUND_TERM_ID(HttpStatus.BAD_REQUEST, "not_found_term_id", "약관 아이디를 찾을 수 없습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
