package kr.modusplant.infrastructure.exception.enums.supers;

import kr.modusplant.infrastructure.http.enums.HttpStatus;

public interface ResponseCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
    boolean isSuccess();
}
