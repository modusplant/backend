package kr.modusplant.global.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.auth.social.app.error.OAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Void>> handleGenericException(HttpServletRequest request, Exception ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    // RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponse<Void>> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred");

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // OAuth 이용 간 예외가 발생한 경우
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<DataResponse<Void>> handleOAuthException(OAuthException ex) {
        return ResponseEntity.status(ex.getStatus()).body(DataResponse.of(ex.getStatus().value(), ex.getMessage()));
    }

    // 메서드의 인자가 유효하지 않은 값일 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid client data");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 검증이 실패한 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid method argument");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 호출된 메서드가 정상적으로 작동할 수 없는 경우
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.CONFLICT.value(), "Not available resource");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // 요청 처리 간 예외가 발생한 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getRootCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case InvalidFormatException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Value cannot be deserialized to expected type");
            }
            case UnrecognizedPropertyException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Body has property that target class do not know");
            }
            case JsonMappingException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Mapping to body and Java object failed");
            }
            case JsonParseException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Parsing body and Java object failed");
            }
            default -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Malformed request body");
            }
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 응답 처리 간 예외가 발생한 경우
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {

        Throwable cause = ex.getRootCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case InvalidFormatException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Value cannot be deserialized to expected type");
            }
            case UnrecognizedPropertyException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Body has property that target class do not know");
            }
            case JsonMappingException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Mapping to body and Java object failed");
            }
            case JsonParseException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Parsing body and Java object failed");
            }
            default -> {
                errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Malformed request body");
            }
        }

        return ResponseEntity.internalServerError().body(errorResponse);
    }

}