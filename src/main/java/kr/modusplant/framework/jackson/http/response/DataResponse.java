package kr.modusplant.framework.jackson.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.exception.enums.GeneralSuccessCode;
import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.HashMap;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> DataResponse<T> of(ErrorCode errorCode, T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = errorCode.getHttpStatus();
        response.code = errorCode.getCode();
        response.message = errorCode.getMessage();
        response.data = data;
        return response;
    }

    public static DataResponse<Void> of(ErrorCode errorCode) {
        DataResponse<Void> response = new DataResponse<>();
        response.status = errorCode.getHttpStatus();
        response.code = errorCode.getCode();
        response.message = errorCode.getMessage();
        return response;
    }

    public static DataResponse<Void> ok() {
        DataResponse<Void> response = new DataResponse<>();
        response.status = GeneralSuccessCode.GENERIC_SUCCESS.getHttpStatus();
        response.code = GeneralSuccessCode.GENERIC_SUCCESS.getCode();
        response.message = GeneralSuccessCode.GENERIC_SUCCESS.getMessage();
        return response;
    }

    public static <T> DataResponse<T> ok(T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = GeneralSuccessCode.GENERIC_SUCCESS.getHttpStatus();
        response.code = GeneralSuccessCode.GENERIC_SUCCESS.getCode();
        response.message = GeneralSuccessCode.GENERIC_SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    @SneakyThrows
    @Override
    public String toString() {
        HashMap<String, Object> map = new HashMap<>(){{
            put("status", status);
            put("code", code);
        }};
        if (message != null) {
            map.put("message", message);
        }
        if (data != null) {
            map.put("data", data);
        }
        return ObjectMapperHolder.getObjectMapper().writeValueAsString(map);
    }

}
