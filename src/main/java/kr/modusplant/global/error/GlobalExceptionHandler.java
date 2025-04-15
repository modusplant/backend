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
    public ResponseEntity<Map<String, Object>> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("status", HttpStatus.BAD_REQUEST.value());
        metaData.put("message", Optional.ofNullable(ex.getMessage()).orElse("An unexpected error occurred"));

        Map<String, Object> response = new HashMap<>();
        response.put("metaData", metaData);

        logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    "Field name: " + error.getField() +
                    ", default message: " + error.getDefaultMessage())
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid client data");
        problemDetail.setDetail("required property missing, invalid format, constraint violation, etc");
        problemDetail.setProperty("fieldErrorList", errors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    // 메서드의 인자가 무효한 값일 경우 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid method argument");
        Optional.ofNullable(ex.getMessage()).ifPresent(value -> problemDetail.setDetail(ex.getMessage()));
        return ResponseEntity.badRequest().body(problemDetail);
    }

    // 호출된 메서드가 정상적으로 작동할 수 없는 경우 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.CONFLICT.value(), "resource is not available");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // JSON 매핑 요청 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMalformedJsonException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid body format");

        Throwable cause = ex.getCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case InvalidFormatException ifx -> {
                String failPoint = getFailLocation(ifx);
                problemDetail.setDetail("value cannot be deserialized to expected type");
                problemDetail.setProperty("error path", failPoint);
            }
            case UnrecognizedPropertyException upx -> {
                problemDetail.setDetail("body has property that target class do not know");
                Optional.ofNullable(upx.getPropertyName()).ifPresent(value -> problemDetail.setProperty("unknown field", value));
                Optional.ofNullable(upx.getKnownPropertyIds()).ifPresent(value -> problemDetail.setProperty("known fields", value));
            }
            case JsonMappingException jmx -> {
                String failPoint = getFailLocation(jmx);
                problemDetail.setDetail("parsing body and Java object failed");
                problemDetail.setProperty("error path", failPoint);

            }
            case null, default -> problemDetail.setDetail("malformed request body");
        }

        return ResponseEntity.badRequest().body(problemDetail);
    }

    private <T extends JsonMappingException> String getFailLocation(T ex) {
        return ex.getPath().stream()
                .map(path -> {
                    if(path.getFieldName() != null) {
                        return path.getFieldName();
                    } else if (path.getIndex() >= 0) {
                        return "[" + path.getIndex() + "]";
                    }
                    return "";
                })
                .filter(str -> !str.isEmpty())
                .collect(Collectors.joining("."))
                .replace(".[", "[");
    }
}