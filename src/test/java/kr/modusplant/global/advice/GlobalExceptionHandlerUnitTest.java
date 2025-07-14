package kr.modusplant.global.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
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
public class GlobalExceptionHandlerUnitTest {

    @Spy
    private GlobalExceptionHandler globalExceptionHandler;

    @DisplayName("IllegalArgumentException 처리")
    @Test
    public void handleIllegalArgumentExceptionTest() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalArgumentException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("IllegalStateException 처리")
    @Test
    public void handleIllegalStateExceptionTest() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalStateException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_STATE.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_STATE.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("MethodArgumentNotValidException 처리")
    @Test
    public void handleMethodArgumentNotValidExceptionTest() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "테스트 객체");
        bindingResult.addError(new FieldError("testObject", "testField", "테스트 메시지"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("testField"));
        assertNull(errorResponse.getData());
    }

    @DisplayName("MethodArgumentTypeMismatchException 처리")
    @Test
    public void handleMethodArgumentTypeMismatchExceptionTest() {
        // given
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        given(ex.getName()).willReturn("testRequestParam");

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INPUT_TYPE_MISMATCH.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.INPUT_TYPE_MISMATCH.getCode(), errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("testRequestParam"));
        assertNull(errorResponse.getData());
    }

    @DisplayName("ConstraintViolationException 처리")
    @Test
    public void handleConstraintViolationExceptionTest() {
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


        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleConstraintViolationException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        System.out.println(errorResponse.getMessage());
        assertEquals(ErrorCode.CONSTRAINT_VIOLATION.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.CONSTRAINT_VIOLATION.getCode(), errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("testFieldName1"));
        assertTrue(errorResponse.getMessage().contains("testFieldName2"));
        assertTrue(errorResponse.getMessage().contains("testFieldName3"));
        assertNull(errorResponse.getData());
    }

    @DisplayName("요청으로 인한 UnrecognizedPropertyException 처리")
    @Test
    void handleUnrecognizedPropertyExceptionOnRequestTest() {
        // given
        UnrecognizedPropertyException upx = new UnrecognizedPropertyException(null, null, null, null, null, null);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", upx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.UNEXPECTED_INPUT.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.UNEXPECTED_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("요청으로 인한 JsonMappingException 처리")
    @Test
    void handleJsonMappingExceptionOnRequestTest() {
        // given
        JsonMappingException jmx = mock(JsonMappingException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", jmx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("요청으로 인한 JsonParseException 처리")
    @Test
    void handleJsonParseExceptionOnRequestTest() {
        // given
        JsonParseException jpx = mock(JsonParseException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", jpx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.INVALID_INPUT.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("HttpMessageNotReadableException 처리")
    @Test
    public void handleHttpMessageNotReadableExceptionTest() {
        // given
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("", mock(HttpMessageNotReadableException.class), inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.MALFORMED_INPUT.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.MALFORMED_INPUT.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("HttpMessageNotWritableException 처리")
    @Test
    public void handleHttpMessageNotWritableExceptionTest() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException();
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("BusinessException 처리")
    @Test
    public void handleBusinessExceptionTest() {
        // given
        BusinessException ex = new BusinessException(ErrorCode.GENERIC_ERROR);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleBusinessException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("RuntimeException 처리")
    @Test
    public void handleRuntimeExceptionTest() {
        // given
        RuntimeException ex = mock(RuntimeException.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleRuntimeException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("Exception 처리")
    @Test
    public void handleGenericExceptionTest() {
        // given
        Exception ex = mock(Exception.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleGenericException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(ErrorCode.GENERIC_ERROR.getHttpStatus().value(), errorResponse.getStatus());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getCode());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

}
