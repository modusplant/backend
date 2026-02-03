package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;

/**
 * 클라이언트가 특정 리소스에 접근할 수 없는 경우에 발생하는 예외입니다.
 * 로그인 된 회원이 다른 회원의 마이페이지에 접근하려는 경우가 해당됩니다.
 * 접근 권한이 없어서 접근할 수 없는 경우는 해당되지 않습니다.
 */
public class NotAccessibleException extends BusinessException {

    private final String target;
    private final String targetId;

    public NotAccessibleException(ErrorCode errorCode, String target, String targetId) {
        super(errorCode);
        this.target = target;
        this.targetId = targetId;
    }

    public NotAccessibleException(ErrorCode errorCode, String target, String targetId, String message) {
        super(errorCode, message);
        this.target = target;
        this.targetId = targetId;
    }

    public NotAccessibleException(ErrorCode errorCode, String target, String targetId, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.target = target;
        this.targetId = targetId;
    }

    public NotAccessibleException(ErrorCode errorCode, String target, String targetId, Throwable cause) {
        super(errorCode, cause);
        this.target = target;
        this.targetId = targetId;
    }

    @Override
    public String getMessage() {
        return String.format("%s [target: %s, targetId: %s]", super.getMessage(), target, targetId);
    }
}
