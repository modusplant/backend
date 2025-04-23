package kr.modusplant.global.app.servlet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static kr.modusplant.global.vo.ResponseMessage.RESPONSE_MESSAGE_200;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> DataResponse<T> of(int status, String message, T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = status;
        response.message = message;
        response.data = data;
        return response;
    }

    public static DataResponse<Void> of(int status, String message) {
        DataResponse<Void> response = new DataResponse<>();
        response.status = status;
        response.message = message;
        return response;
    }

    public static DataResponse<Void> ok() {
        DataResponse<Void> response = new DataResponse<>();
        response.status = HttpStatus.OK.value();
        response.message = RESPONSE_MESSAGE_200;
        return response;
    }

    public static <T> DataResponse<T> ok(T data) {
        DataResponse<T> response = new DataResponse<>();
        response.status = HttpStatus.OK.value();
        response.message = RESPONSE_MESSAGE_200;
        response.data = data;
        return response;
    }

}
