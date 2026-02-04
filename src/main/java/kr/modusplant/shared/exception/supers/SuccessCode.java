package kr.modusplant.shared.exception.supers;

public interface SuccessCode {
    int getHttpStatus();
    String getCode();
    String getMessage();
}
