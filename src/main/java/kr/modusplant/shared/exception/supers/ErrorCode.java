package kr.modusplant.shared.exception.supers;

public interface ErrorCode {
    int getHttpStatus();
    String getCode();
    String getMessage();
}
