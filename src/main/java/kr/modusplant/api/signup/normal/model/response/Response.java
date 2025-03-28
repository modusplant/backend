package kr.modusplant.api.signup.normal.model.response;


import lombok.Getter;

@Getter
public class Response<T> {
    private Metadata metadata;
    private T data;

    public static <T> Response<T> createWithData(int status, String message, T data) {
        Response<T> response = new Response<>();
        Metadata metadata = new Metadata();
        metadata.setStatus(status);
        metadata.setMessage(message);

        response.metadata = metadata;
        response.data = data;

        return response;
    }

    public static <T> Response<T> createWithoutData(int status, String message) {
        Response<T> response = new Response<>();
        Metadata metadata = new Metadata();
        metadata.setStatus(status);
        metadata.setMessage(message);

        response.metadata = metadata;
        return response;
    }
}
