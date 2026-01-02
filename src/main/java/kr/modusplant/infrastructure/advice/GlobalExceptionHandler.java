package kr.modusplant.infrastructure.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 메서드의 인자가 유효하지 않은 값일 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalArgumentException() {
        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.INVALID_INPUT.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.INVALID_INPUT));
    }

    // 호출된 메서드가 정상적으로 작동할 수 없는 경우
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalStateException() {
        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.INVALID_STATE.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.INVALID_STATE));
    }

    // 메서드의 인자가 유효하지 않은 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().getFirst();

        log.error("FieldError of MethodArgumentNotValidException: {}", fieldError);

        if(fieldError != null) {
            return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.INVALID_INPUT.getHttpStatus().getValue()))
                    .body(DataResponse.of(GeneralErrorCode.INVALID_INPUT));
        } else {
            return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.INVALID_INPUT.getHttpStatus().getValue()))
                    .body(DataResponse.of(GeneralErrorCode.INVALID_INPUT));
        }
    }

    // 메소드의 타입과 요청 값의 타입이 불일치하는 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        if(!ex.getName().isBlank()) {
            log.error("FieldError of MethodArgumentTypeMismatchException: {}", ex.getName());
        }

        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.MISMATCH_INPUT_TYPE.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.MISMATCH_INPUT_TYPE));
    }

    // 검증이 실패한 경우
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DataResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        if(constraintViolations != null) {
            List<String> invalidPropertyNames = constraintViolations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .toList();

            log.error("invalidPropertyNames of MethodArgumentTypeMismatchException: {}", invalidPropertyNames);

        }
        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.CONSTRAINT_VIOLATION.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.CONSTRAINT_VIOLATION));
    }

    // 요청 처리 간 예외가 발생한 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getRootCause();
        DataResponse<Void> errorResponse;

        switch (cause) {
            case UnrecognizedPropertyException ignored -> errorResponse = DataResponse.of(GeneralErrorCode.UNEXPECTED_INPUT);
            case JsonMappingException ignored -> errorResponse = DataResponse.of(GeneralErrorCode.INVALID_INPUT);
            case JsonParseException ignored -> errorResponse = DataResponse.of(GeneralErrorCode.INVALID_INPUT);
            case null, default -> errorResponse = DataResponse.of(GeneralErrorCode.MALFORMED_INPUT);
        }

        return ResponseEntity.status(errorResponse.getStatus())
                .body(errorResponse);
    }

    // 응답 처리 간 예외가 발생한 경우
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotWritableException() {
        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.GENERIC_ERROR.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.GENERIC_ERROR));
    }

    // BusinessException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DataResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getErrorCode().getHttpStatus().getValue()))
                .body(DataResponse.of(ex.getErrorCode()));
    }

    // RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponse<Void>> handleRuntimeException(HttpServletRequest ignoredRequest, RuntimeException ignoredEx) {
        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.GENERIC_ERROR.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.GENERIC_ERROR));
    }

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Void>> handleGenericException(HttpServletRequest ignoredRequest, Exception ignoredEx) {
        return ResponseEntity.status(HttpStatus.valueOf(GeneralErrorCode.GENERIC_ERROR.getHttpStatus().getValue()))
                .body(DataResponse.of(GeneralErrorCode.GENERIC_ERROR));
    }
}