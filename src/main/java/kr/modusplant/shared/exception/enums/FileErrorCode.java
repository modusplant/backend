package kr.modusplant.shared.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {

    FILE_LOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR.value(),"file_load_failure","파일을 불러오는 데 실패했습니다"),
    INVALID_FILE_INPUT(HttpStatus.BAD_REQUEST.value(),"invalid_file_input","파일 입력이 올바르지 않습니다"),
    UNSUPPORTED_FILE(HttpStatus.FORBIDDEN.value(), "unsupported_file", "지원되지 않는 파일 타입입니다"),
    FILE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST.value(),"file_limit_exceeded","파일 개수 또는 크기 제한을 초과했습니다"),
    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
