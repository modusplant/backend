package kr.modusplant.domain.exception.enums.supers;

import kr.modusplant.domain.enums.HttpStatus;

public interface ResponseCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
    boolean isSuccess();
}
