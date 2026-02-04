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
import kr.modusplant.shared.exception.model.DynamicErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 메서드의 인자가 유효하지 않은 값일 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalArgumentException() {
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.INVALID_INPUT));
    }

    // 호출된 메서드가 정상적으로 작동할 수 없는 경우
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalStateException() {
        return ResponseEntity.status(GeneralErrorCode.INVALID_STATE.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.INVALID_STATE));
    }

    // 메서드의 인자가 유효하지 않은 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().getFirst();

        log.error("FieldError of MethodArgumentNotValidException: {}", fieldError);

        String message = ex.getBindingResult().getAllErrors()
                .getFirst().getDefaultMessage();

        if (message == null || message.isBlank()) {
            return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT.getHttpStatus())
                    .body(DataResponse.of(GeneralErrorCode.INVALID_INPUT));
        } else {
            DynamicErrorCode dynamicErrorCode = DynamicErrorCode.create(GeneralErrorCode.INVALID_INPUT, message);
            return ResponseEntity.status(dynamicErrorCode.getHttpStatus())
                    .body(DataResponse.of(dynamicErrorCode));
        }

    }

    // 메소드의 타입과 요청 값의 타입이 불일치하는 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        if(!ex.getName().isBlank()) {
            log.error("FieldError of MethodArgumentTypeMismatchException: {}", ex.getName());
        }

        return ResponseEntity.status(GeneralErrorCode.MISMATCH_INPUT_TYPE.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.MISMATCH_INPUT_TYPE));
    }

    // 검증이 실패한 경우
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DataResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        Optional<String> firstMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst();

        if(constraintViolations != null) {
            List<String> invalidPropertyNames = constraintViolations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .toList();

            log.error("invalidPropertyNames of MethodArgumentTypeMismatchException: {}", invalidPropertyNames);
        }

        if(firstMessage.isPresent()) {
            DynamicErrorCode dynamicErrorCode = DynamicErrorCode.create(GeneralErrorCode.CONSTRAINT_VIOLATION, firstMessage.get());
            return ResponseEntity.status(dynamicErrorCode.getHttpStatus())
                    .body(DataResponse.of(dynamicErrorCode));
        } else {
            return ResponseEntity.status(GeneralErrorCode.CONSTRAINT_VIOLATION.getHttpStatus())
                    .body(DataResponse.of(GeneralErrorCode.CONSTRAINT_VIOLATION));
        }
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
        return ResponseEntity.status(GeneralErrorCode.GENERIC_ERROR.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.GENERIC_ERROR));
    }

    // 동시성 문제가 발생한 경우
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<DataResponse<Void>> handleObjectOptimisticLockingFailureException() {
        return ResponseEntity.status(GeneralErrorCode.FAILURE_OPTIMISTIC_LOCKING.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.FAILURE_OPTIMISTIC_LOCKING));
    }

    // BusinessException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DataResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getErrorCode().getHttpStatus()))
                .body(DataResponse.of(ex.getErrorCode()));
    }

    // RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponse<Void>> handleRuntimeException(HttpServletRequest ignoredRequest, RuntimeException ignoredEx) {
        return ResponseEntity.status(GeneralErrorCode.GENERIC_ERROR.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.GENERIC_ERROR));
    }

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Void>> handleGenericException(HttpServletRequest ignoredRequest, Exception ignoredEx) {
        return ResponseEntity.status(GeneralErrorCode.GENERIC_ERROR.getHttpStatus())
                .body(DataResponse.of(GeneralErrorCode.GENERIC_ERROR));
    }
}