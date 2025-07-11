package kr.modusplant.global.app.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.modusplant.global.enums.SuccessCode;
import kr.modusplant.global.enums.supers.ResponseCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> DataResponse<T> of(ResponseCode responseCode, T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = responseCode.getHttpStatus().value();
        response.code = responseCode.getCode();
        response.message = responseCode.getMessage();
        response.data = data;
        return response;
    }

    public static DataResponse<Void> of(ResponseCode responseCode) {
        DataResponse<Void> response = new DataResponse<>();
        response.status = responseCode.getHttpStatus().value();
        response.code = responseCode.getCode();
        response.message = responseCode.getMessage();
        return response;
    }

    public static DataResponse<Void> ofErrorFieldName(ResponseCode responseCode, String errorFieldName) {
        DataResponse<Void> response = new DataResponse<>();
        response.status = responseCode.getHttpStatus().value();
        response.code = responseCode.getCode();
        response.message = responseCode.getMessage() + ". 원인: " + errorFieldName;
        return response;
    }

    public static DataResponse<Void> ofErrorFieldNames(ResponseCode responseCode, Collection<?> errorFieldNames) {
        DataResponse<Void> response = new DataResponse<>();
        response.status = responseCode.getHttpStatus().value();
        response.code = responseCode.getCode();
        response.message = responseCode.getMessage() + ". 원인: " + generateErrorDetail(errorFieldNames);
        return response;
    }

    public static DataResponse<Void> ok() {
        DataResponse<Void> response = new DataResponse<>();
        response.status = SuccessCode.GENERIC_SUCCESS.getHttpStatus().value();
        response.code = SuccessCode.GENERIC_SUCCESS.getCode();
        response.message = SuccessCode.GENERIC_SUCCESS.getMessage();
        return response;
    }

    public static <T> DataResponse<T> ok(T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = SuccessCode.GENERIC_SUCCESS.getHttpStatus().value();
        response.code = SuccessCode.GENERIC_SUCCESS.getCode();
        response.message = SuccessCode.GENERIC_SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    private static String generateErrorDetail(Collection<?> errorFieldNames) {
        String arrangedNames = errorFieldNames.stream()
                .map(fieldName -> fieldName + ", ")
                .collect(Collectors.joining());
        return arrangedNames.substring(0, arrangedNames.length() - 2);
    }

}
