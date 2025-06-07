package kr.modusplant.modules.auth.social.error;

import kr.modusplant.global.enums.ResponseMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class OAuthException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    private static final Map<HttpStatus,String> STATUS_MESSAGES = Map.of(
            HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or missing authentication credentials",
            HttpStatus.BAD_REQUEST, ResponseMessage.RESPONSE_MESSAGE_400.getValue(),
            HttpStatus.INTERNAL_SERVER_ERROR, ResponseMessage.RESPONSE_MESSAGE_500.getValue()
    );

    public OAuthException(HttpStatus status) {
        super(STATUS_MESSAGES.get(status));
        this.status = status;
        this.message =  STATUS_MESSAGES.get(status);
    }
}
