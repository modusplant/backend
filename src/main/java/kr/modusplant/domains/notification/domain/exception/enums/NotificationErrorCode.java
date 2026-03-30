package kr.modusplant.domains.notification.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    EMPTY_NOTIFICATION_ID(HttpStatus.BAD_REQUEST.value(), "empty_notification_id","알림 id가 비어 있습니다. "),
    INVALID_NOTIFICATION_ID(HttpStatus.BAD_REQUEST.value(), "invalid_notification_id", "알림 id가 유효하지 않습니다. "),
    EMPTY_RECIPIENT_ID(HttpStatus.BAD_REQUEST.value(), "empty_recipient_id", "알림 수신자 id가 비어 있습니다. "),
    INVALID_RECIPIENT_ID(HttpStatus.BAD_REQUEST.value(), "invalid_recipient_id", "알림 수신자 id가 유효하지 않습니다. "),
    EMPTY_ACTOR_ID(HttpStatus.BAD_REQUEST.value(), "empty_actor_id", "알림 행위자 id가 비어 있습니다. "),
    INVALID_ACTOR_ID(HttpStatus.BAD_REQUEST.value(), "invalid_actor_id", "알림 행위자 id가 유효하지 않습니다."),
    EMPTY_NOTIFICATION_ACTION(HttpStatus.BAD_REQUEST.value(), "empty_notification_action", "알림 발생 액션이 비어 있습니다. "),
//    INVALID_NOTIFICATION_ACTION(HttpStatus.BAD_REQUEST.value(), "invalid_notification_action", "알림 발생 액션이 유효하지 않습니다. "),
    EMPTY_NOTIFICATION_STATUS(HttpStatus.BAD_REQUEST.value(), "empty_notification_status", "알림 상태가 비어 있습니다. "),
//    INVALID_NOTIFICATION_STATUS(HttpStatus.BAD_REQUEST.value(), "invalid_notification_status", "알림 상태가 유효하지 않습니다. "),
    EMPTY_POST_ID(HttpStatus.BAD_REQUEST.value(), "empty_post_id", "게시글 id가 비어 있습니다. "),
    INVALID_POST_ID(HttpStatus.BAD_REQUEST.value(), "invalid_post_id", "게시글 id가 유효하지 않습니다. "),
    EMPTY_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "empty_comment_path", "댓글 경로가 비어 있습니다. "),
    INVALID_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_comment_path", "댓글 경로가 유효하지 않습니다. "),
    EMPTY_NOTIFICATION_CONTENT(HttpStatus.BAD_REQUEST.value(), "empty_notification_content", "알림 컨텐츠가 비어 있습니다. "),
    INVALID_NOTIFICATION_CONTENT(HttpStatus.BAD_REQUEST.value(),"invalid_notification_content", "알림 컨텐츠가 유효하지 않습니다. ");

    private final int httpStatus;
    private final String code;
    private final String message;
}
