package kr.modusplant.framework.aws.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import kr.modusplant.shared.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AWSErrorCode implements ResponseCode {
    NOT_FOUND_FILE_KEY_ON_S3(HttpStatus.INTERNAL_SERVER_ERROR, "not_found_file_key_on_s3", "저장소에서 파일을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
