package kr.modusplant.infrastructure.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

import java.util.Collections;
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
    @DisplayName("IllegalArgumentException에 응답 반환")
    public void testHandleIllegalArgumentException_givenIllegalArgumentException_willReturnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalArgumentException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("IllegalStateException에 응답 반환")
    public void testHandleIllegalStateException_givenIllegalStateException_willReturnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalStateException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.INVALID_STATE.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.INVALID_STATE.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("필드 바인딩 오류에 응답 반환")
    public void testHandleMethodArgumentNotValidException_givenFieldBindingError_willReturnResponse() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "테스트 객체");
        bindingResult.addError(new FieldError("testObject", "testField", "테스트 메시지"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("문제 있는 메시지에 응답 반환")
    public void testHandleMethodArgumentNotValidException_givenProblematicMessage_willReturnResponse(String message) {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "테스트 객체");
        bindingResult.addError(new FieldError("testObject", "testField", message));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"testRequestParam"})
    @DisplayName("타입 불일치에 응답 반환")
    public void testHandleMethodArgumentTypeMismatchException_givenTypeMismatch_willReturnResponse(String name) {
        // given
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        given(ex.getName()).willReturn(name);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.MISMATCH_INPUT_TYPE.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.MISMATCH_INPUT_TYPE.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("제약 위반에 응답 반환")
    public void testHandleConstraintViolationException_givenConstraintViolation_willReturnResponse() {
        // given
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> testViolations = new HashSet<>(Collections.singletonList(violation));

        given(ex.getConstraintViolations()).willReturn(testViolations);
        given(violation.getPropertyPath()).willReturn(PathImpl.createPathFromString("testFieldName"));
        given(violation.getMessage()).willReturn("Test message");

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleConstraintViolationException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.CONSTRAINT_VIOLATION.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.CONSTRAINT_VIOLATION.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("메시지 없는 제약 위반에 응답 반환")
    public void testHandleConstraintViolationException_givenNoMessage_willReturnResponse() {
        // given
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> testViolations = new HashSet<>();

        given(ex.getConstraintViolations()).willReturn(testViolations);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleConstraintViolationException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.CONSTRAINT_VIOLATION.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.CONSTRAINT_VIOLATION.getCode(), errorResponse.getCode());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("UnrecognizedPropertyException에 응답 반환")
    void testHandleHttpMessageNotReadableException_givenUnrecognizedPropertyException_willReturnResponse() {
        // given
        UnrecognizedPropertyException upx = new UnrecognizedPropertyException(null, null, null, null, null, null);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", upx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.UNEXPECTED_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.UNEXPECTED_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("JsonMappingException에 응답 반환")
    void testHandleHttpMessageNotReadableException_givenJsonMappingException_willReturnResponse() {
        // given
        JsonMappingException jmx = mock(JsonMappingException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", jmx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("JsonParseException에 응답 반환")
    void testHandleHttpMessageNotReadableException_givenJsonParseException_willReturnResponse() {
        // given
        JsonParseException jpx = mock(JsonParseException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", jpx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.INVALID_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("일반 원인의 HttpMessageNotReadableException에 응답 반환")
    public void testHandleHttpMessageNotReadableException_givenGenericCause_willReturnResponse() {
        // given
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("", mock(HttpMessageNotReadableException.class), inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.MALFORMED_INPUT.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.MALFORMED_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("HttpMessageNotWritableException에 응답 반환")
    public void testHandleHttpMessageNotWritableException_givenHttpMessageNotWritableException_willReturnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("낙관적 락 실패에 응답 반환")
    public void testHandleObjectOptimisticLockingFailureException_givenOptimisticLockingFailure_willReturnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleObjectOptimisticLockingFailureException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.FAILURE_OPTIMISTIC_LOCKING.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.FAILURE_OPTIMISTIC_LOCKING.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("BusinessException에 응답 반환")
    public void testHandleBusinessException_givenBusinessException_willReturnResponse() {
        // given
        BusinessException ex = new BusinessException(GeneralErrorCode.GENERIC_ERROR);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleBusinessException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("RuntimeException에 응답 반환")
    public void testHandleRuntimeException_givenRuntimeException_willReturnResponse() {
        // given
        RuntimeException ex = mock(RuntimeException.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleRuntimeException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("Exception에 응답 반환")
    public void testHandleGenericException_givenException_willReturnResponse() {
        // given
        Exception ex = mock(Exception.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleGenericException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getHttpStatus(), errorResponse.getStatus());
        assertEquals(GeneralErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }
}
