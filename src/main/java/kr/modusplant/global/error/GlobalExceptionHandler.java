package kr.modusplant.global.error;

import kr.modusplant.api.crud.model.response.SingleDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<SingleDataResponse<Void>> handleOAuthException(OAuthException ex) {
        return ResponseEntity.status(ex.getStatus()).body(SingleDataResponse.fail(ex.getStatus().value(),ex.getMessage()));
    }
}
