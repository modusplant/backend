package kr.modusplant.shared.exception.enums.supers;

public interface ErrorCode {
    int getHttpStatus();
    String getCode();
    String getMessage();
    boolean isSuccess();
}
