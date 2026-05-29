package kr.modusplant.shared.framework.aws.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AWSErrorCode implements ErrorCode {
    NOT_FOUND_IMAGE_FILE_KEY(HttpStatus.BAD_REQUEST.value(), "not_found_image_file_key", "이미지 파일 키가 존재하지 않습니다. "),
    NOT_FOUND_IMAGE_FILE_KEYS(HttpStatus.BAD_REQUEST.value(), "not_found_image_file_keys", "이미지 파일 키가 존재하지 않습니다. "),
    NOT_FOUND_FILE_KEY_ON_S3(HttpStatus.INTERNAL_SERVER_ERROR.value(), "not_found_file_key_on_s3", "Amazon S3에서 파일 키를 찾을 수 없습니다. ");

    private final int httpStatus;
    private final String code;
    private final String message;
}
