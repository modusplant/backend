package kr.modusplant.global.app.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.modusplant.global.enums.ResponseCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static kr.modusplant.global.enums.ResponseMessage.RESPONSE_MESSAGE_200;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> DataResponse<T> of(int status, String code, String message, T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = status;
        response.code = code;
        response.message = message;
        response.data = data;
        return response;
    }

    public static DataResponse<Void> of(int status, String code, String message) {
        DataResponse<Void> response = new DataResponse<>();
        response.status = status;
        response.code = code;
        response.message = message;
        return response;
    }

    public static DataResponse<Void> ok() {
        DataResponse<Void> response = new DataResponse<>();
        response.status = HttpStatus.OK.value();
        response.code = ResponseCode.OK.name();
        response.message = RESPONSE_MESSAGE_200.getValue();
        return response;
    }

    public static <T> DataResponse<T> ok(T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = HttpStatus.OK.value();
        response.code = ResponseCode.OK.name();
        response.message = RESPONSE_MESSAGE_200.getValue();
        response.data = data;
        return response;
    }

}
