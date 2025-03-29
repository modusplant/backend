package kr.modusplant.global.app.servlet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse<T> {
    private Metadata metadata;
    private T data;

    // 팩토리 메서드
    public static <T> DataResponse<T> of(int status, String message, T data) {
        DataResponse<T> response = new DataResponse<>();
        response.metadata = new Metadata(status, message);
        response.data = data;
        return response;
    }

    public static DataResponse<Void> of(int status, String message) {
        DataResponse<Void> response = new DataResponse<>();
        response.metadata = new Metadata(status, message);
        return response;
    }
}
