package kr.modusplant.domains.record.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements ErrorCode {
    RECORD_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "record_access_denied", "기록에 대한 접근 권한이 없습니다."),
    INVALID_VIDEO_DURATION(HttpStatus.BAD_REQUEST.value(), "invalid_video_duration", "영상 길이가 허용 범위를 벗어났습니다."),
    UNSUPPORTED_VIDEO_CODEC(HttpStatus.BAD_REQUEST.value(), "unsupported_video_codec", "지원되지 않는 영상 코덱입니다."),
    VIDEO_RESOLUTION_TOO_LARGE(HttpStatus.BAD_REQUEST.value(), "video_resolution_too_large", "영상 해상도가 허용 범위를 초과했습니다."),
    VIDEO_FILE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "video_file_not_found", "업로드된 영상 파일을 찾을 수 없습니다."),
    THUMBNAIL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "thumbnail_generation_failed", "썸네일 생성에 실패했습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
