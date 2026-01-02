package kr.modusplant.shared.exception.enums.supers;

public interface ResponseCode {
    int getHttpStatus();
    String getCode();
    String getMessage();
    boolean isSuccess();
}
