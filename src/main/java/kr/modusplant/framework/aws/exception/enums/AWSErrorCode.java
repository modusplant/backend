package kr.modusplant.framework.aws.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AWSErrorCode implements ErrorCode {
    NOT_FOUND_FILE_KEY_ON_S3(HttpStatus.INTERNAL_SERVER_ERROR.value(), "not_found_file_key_on_s3", "저장소에서 파일을 찾을 수 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
