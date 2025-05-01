package kr.modusplant.global.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUnitTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void handleRuntimeException_givenValidCondition_thenReturnMap() {
        // given
        RuntimeException ex = mock(RuntimeException.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleRuntimeException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    public void handleGenericException_givenValidCondition_thenReturnMap() {
        // given
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        Exception ex = mock(Exception.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleGenericException(servletRequest, ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    public void handleIllegalArgumentException_givenValidCondition_thenReturnProblemDetail() {
        // given
        IllegalArgumentException ex = mock(IllegalArgumentException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalArgumentException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid method argument", errorResponse.getMessage());
    }

    @Test
    public void handleIllegalStateException_givenValidCondition_thenReturnProblemDetail() {
        // given
        IllegalStateException ex = mock(IllegalStateException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalStateException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("resource is not available", errorResponse.getMessage());
    }

    @Test
    public void handleValidationException_givenValidCondition_thenReturnProblemDetail() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleValidationException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid client data", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    public void handleValidationException_givenEmptyFieldErrors_thenReturnProblemDetail() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleValidationException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid client data", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    public void handleMalformedJsonException_givenValidCondition_thenReturnProblemDetail() {
        // given
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMalformedJsonException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("malformed request body", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    public void handleMalformedJsonException_givenInvalidFormatException_thenReturnProblemDetail() {
        // given
        InvalidFormatException ifx = new InvalidFormatException(null, "Invalid format", "value", Integer.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", ifx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMalformedJsonException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("value cannot be deserialized to expected type", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test void handleMalformedJsonException_givenUnrecognizedPropertyException_thenReturnProblemDetail() {
        // given
        UnrecognizedPropertyException upx = new UnrecognizedPropertyException(null, null, null, null, null, null);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", upx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMalformedJsonException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("body has property that target class do not know", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }
}
