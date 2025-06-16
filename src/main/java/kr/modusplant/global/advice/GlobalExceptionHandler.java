package kr.modusplant.global.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.global.error.DomainException;
import kr.modusplant.modules.auth.social.error.OAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static kr.modusplant.global.enums.ResponseCode.BAD_REQUEST;
import static kr.modusplant.global.enums.ResponseCode.INTERNAL_SERVER_ERROR;
import static kr.modusplant.global.enums.ResponseMessage.RESPONSE_MESSAGE_400;
import static kr.modusplant.global.enums.ResponseMessage.RESPONSE_MESSAGE_500;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Void>> handleGenericException(HttpServletRequest ignoredRequest, Exception ignoredEx) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR.getValue(), RESPONSE_MESSAGE_500.getValue());
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    // RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponse<Void>> handleRuntimeException(HttpServletRequest ignoredRequest, RuntimeException ignoredEx) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST.getValue(), RESPONSE_MESSAGE_400.getValue());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 도메인 기능에서 예외가 발생한 경우
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<DataResponse<Void>> handleDomainException(HttpServletRequest ignoredRequest, DomainException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(ex.getStatus().value(), ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus().value()).body(errorResponse);
    }

    // OAuth 이용 간에 예외가 발생한 경우
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<DataResponse<Void>> handleOAuthException(OAuthException ex) {
        return ResponseEntity.status(ex.getStatus().value()).body(DataResponse.of(ex.getStatus().value(), "EXTERNAL_API_ERROR", ex.getMessage()));
    }

    // 메소드의 타입과 요청 값의 타입이 불일치하는 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ignoredEx) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "TYPE_MISMATCH", "서식이 올바르지 않아 값을 처리할 수 없습니다.");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 검증이 실패한 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().getFirst();
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "CONSTRAINT_VIOLATION", fieldError.getDefaultMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 메서드의 인자가 유효하지 않은 값일 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "CONSTRAINT_VIOLATION", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 호출된 메서드가 정상적으로 작동할 수 없는 경우
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        DataResponse<Void> errorResponse = DataResponse.of(HttpStatus.SERVICE_UNAVAILABLE.value(), "NOT_AVAILABLE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // 요청 처리 간 예외가 발생한 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getRootCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case InvalidFormatException ignored -> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "INVALID_JSON_FORMAT", "유효하지 않은 JSON입니다.");
            case UnrecognizedPropertyException ignored -> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "NOT_KNOWING_PROPERTY", "서버가 알지 못하는 데이터가 포함되어 있습니다.");
            case JsonMappingException ignored -> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(),"JSON_MAPPING_FAILURE", "JSON의 Java 객체로의 매핑이 실패하였습니다.");
            case JsonParseException ignored -> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "JSON_PARSING_FAILURE", "Java 객체의 JSON으로의 파싱이 실패하였습니다.");
            default -> errorResponse = DataResponse.of(HttpStatus.BAD_REQUEST.value(), "MALFORMED_REQUEST", "요청 바디의 서식이 올바르지 않습니다.");
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 응답 처리 간 예외가 발생한 경우
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {

        Throwable cause = ex.getRootCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case InvalidFormatException ignored -> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVALID_JSON_FORMAT", "유효하지 않은 JSON입니다.");
            case UnrecognizedPropertyException ignored -> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "NOT_KNOWING_PROPERTY", "서버가 알지 못하는 데이터가 포함되어 있습니다.");
            case JsonMappingException ignored -> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(),"JSON_MAPPING_FAILURE", "JSON의 Java 객체로의 매핑이 실패하였습니다.");
            case JsonParseException ignored -> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "JSON_PARSING_FAILURE", "Java 객체의 JSON으로의 파싱이 실패하였습니다.");
            default -> errorResponse = DataResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "MALFORMED_REQUEST", "요청 바디의 서식이 올바르지 않습니다.");
        }

        return ResponseEntity.internalServerError().body(errorResponse);
    }
}