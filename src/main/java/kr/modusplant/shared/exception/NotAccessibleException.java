package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;

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
