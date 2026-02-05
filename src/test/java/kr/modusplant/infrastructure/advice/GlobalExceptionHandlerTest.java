package kr.modusplant.infrastructure.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Spy
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("IllegalArgumentException으로 전역 예외 핸들러 호출")
    public void testHandleIllegalArgumentException_givenValidGlobalExceptionHandler_returnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalArgumentException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("IllegalStateException으로 전역 예외 핸들러 호출")
    public void testHandleIllegalStateException_givenValidGlobalExceptionHandler_returnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalStateException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_STATE.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_STATE.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException으로 전역 예외 핸들러 호출")
    public void testHandleMethodArgumentNotValidException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "테스트 객체");
        bindingResult.addError(new FieldError("testObject", "testField", "테스트 메시지"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException으로 전역 예외 핸들러 호출")
    public void testHandleMethodArgumentTypeMismatchException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        given(ex.getName()).willReturn("testRequestParam");

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INPUT_TYPE_MISMATCH.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.INPUT_TYPE_MISMATCH.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("ConstraintViolationException으로 전역 예외 핸들러 호출")
    public void testHandleConstraintViolationException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        ConstraintViolationException ex = mock(ConstraintViolationException.class);

        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation3 = mock(ConstraintViolation.class);

        Set<ConstraintViolation<?>> testViolations =
                new HashSet<>(Arrays.asList(violation1, violation2, violation3));

        given(ex.getConstraintViolations()).willReturn(testViolations);
        given(violation1.getPropertyPath()).willReturn(PathImpl.createPathFromString("testFieldName1"));
        given(violation2.getPropertyPath()).willReturn(PathImpl.createPathFromString("testFieldName2"));
        given(violation3.getPropertyPath()).willReturn(PathImpl.createPathFromString("testFieldName3"));
        given(violation1.getMessage()).willReturn("Test message 1");

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleConstraintViolationException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.CONSTRAINT_VIOLATION.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.CONSTRAINT_VIOLATION.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("UnrecognizedPropertyException으로 전역 예외 핸들러 호출")
    void testHandleHttpMessageNotReadableException_givenUnrecognizedPropertyException_returnResponse() {
        // given
        UnrecognizedPropertyException upx = new UnrecognizedPropertyException(null, null, null, null, null, null);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", upx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.UNEXPECTED_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.UNEXPECTED_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("JsonMappingException으로 전역 예외 핸들러 호출")
    void testHandleHttpMessageNotReadableException_givenJsonMappingException_returnResponse() {
        // given
        JsonMappingException jmx = mock(JsonMappingException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", jmx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("JsonParseException으로 전역 예외 핸들러 호출")
    void testHandleHttpMessageNotReadableException_givenJsonParseException_returnResponse() {
        // given
        JsonParseException jpx = mock(JsonParseException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", jpx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("HttpMessageNotReadableException으로 전역 예외 핸들러 호출")
    public void testHandleHttpMessageNotReadableException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("", mock(HttpMessageNotReadableException.class), inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.MALFORMED_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.MALFORMED_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("HttpMessageNotWritableException으로 전역 예외 핸들러 호출")
    public void testHandleHttpMessageNotWritableException_givenValidGlobalExceptionHandler_returnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

//    @Test
//    @DisplayName("ObjectOptimisticLockingFailureException으로 전역 예외 핸들러 호출")
//    public void testHandleObjectOptimisticLockingFailureException_givenValidGlobalExceptionHandler_returnResponse() {
//        // given & when
//        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException();
//        DataResponse<Void> errorResponse = response.getBody();
//
//        // then
//        assertNotNull(errorResponse);
//        assertEquals(GeneralErrorCode.FAILURE_OPTIMISTIC_LOCKING.getHttpStatus(), errorResponse.getStatus());
//        assertEquals(GeneralErrorCode.FAILURE_OPTIMISTIC_LOCKING.getCode(), errorResponse.getCode());
//        assertNotNull(errorResponse.getMessage());
//        assertNull(errorResponse.getData());
//    }

    @Test
    @DisplayName("BusinessException으로 전역 예외 핸들러 호출")
    public void testHandleBusinessException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        BusinessException ex = new BusinessException(ErrorCode.GENERIC_ERROR);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleBusinessException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("RuntimeException으로 전역 예외 핸들러 호출")
    public void testHandleRuntimeException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        RuntimeException ex = mock(RuntimeException.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleRuntimeException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("Exception으로 전역 예외 핸들러 호출")
    public void testHandleException_givenValidGlobalExceptionHandler_returnResponse() {
        // given
        Exception ex = mock(Exception.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleGenericException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }
}
