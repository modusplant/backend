package kr.modusplant.global.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // OAuthException 처리
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<DataResponse<Void>> handleOAuthException(OAuthException ex) {
        return ResponseEntity.status(ex.getStatus()).body(DataResponse.of(ex.getStatus().value(),ex.getMessage()));
    }

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponse<Void>> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred");

        logger.error(ex.getMessage(), ex);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 그 외 모든 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(HttpServletRequest request, Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("message", "Internal Server Error: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // 검증로직 실패 시 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid client data");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 메서드의 인자가 무효한 값일 경우 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid method argument");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 호출된 메서드가 정상적으로 작동할 수 없는 경우 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.CONFLICT.value(), "resource is not available");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // JSON 매핑 요청 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DataResponse<Void>> handleMalformedJsonException(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case InvalidFormatException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "value cannot be deserialized to expected type");
            }
            case UnrecognizedPropertyException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "body has property that target class do not know");
            }
            case JsonMappingException ignored -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "parsing body and Java object failed");
            }
            case null, default -> {
                errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "malformed request body");
            }
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }
}