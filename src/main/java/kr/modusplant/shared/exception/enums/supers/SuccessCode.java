package kr.modusplant.shared.exception.enums.supers;

public interface SuccessCode {
    int getHttpStatus();
    String getCode();
    String getMessage();
}
