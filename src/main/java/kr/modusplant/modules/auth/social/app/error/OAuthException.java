package kr.modusplant.modules.auth.social.app.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class OAuthException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    private static final Map<HttpStatus,String> STATUS_MESSAGES = Map.of(
            HttpStatus.BAD_REQUEST, "Bad Request: Failed due to client error",
            HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or missing authentication credentials",
            HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: An unexpected error occurred on the server"
    );

    public OAuthException(HttpStatus status) {
        super(STATUS_MESSAGES.get(status));
        this.status = status;
        this.message =  STATUS_MESSAGES.get(status);
    }
}
