package kr.modusplant.shared.exception.enums.supers;

import kr.modusplant.shared.http.enums.HttpStatus;

public interface ResponseCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
    boolean isSuccess();
}
