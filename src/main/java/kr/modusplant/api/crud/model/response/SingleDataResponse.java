package kr.modusplant.api.crud.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import kr.modusplant.global.app.servlet.response.Metadata;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleDataResponse<T> {
    private Metadata metadata;
    private T data;

    // 팩토리 메서드
    public static <T> SingleDataResponse<T> success(int status, String message, T data) {
        SingleDataResponse<T> response = new SingleDataResponse<>();
        response.metadata = new Metadata(status, message);
        response.data = data;

        return response;
    }

    public static SingleDataResponse<Void> fail(int status, String message) {
        SingleDataResponse<Void> response = new SingleDataResponse<>();
        response.metadata = new Metadata(status, message);

        return response;
    }
}
