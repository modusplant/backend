package kr.modusplant.global.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.auth.social.app.error.OAuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUnitTest {

    @Spy
    private GlobalExceptionHandler globalExceptionHandler;

    @DisplayName("Exception 처리")
    @Test
    public void handleGenericExceptionTest() {
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertNotNull(errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("OAuthException 처리")
    @Test
    public void handleOAuthExceptionTest() {
        // given
        OAuthException ex = new OAuthException(HttpStatus.BAD_REQUEST);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleOAuthException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(ex.getMessage(), errorResponse.getMessage());
    }

    @DisplayName("IllegalArgumentException 처리")
    @Test
    public void handleIllegalArgumentExceptionTest() {
        // given
        IllegalArgumentException ex = mock(IllegalArgumentException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalArgumentException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid client data", errorResponse.getMessage());
    }

    @DisplayName("MethodArgumentNotValidException 처리")
    @Test
    public void handleMethodArgumentNotValidExceptionTest() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid method argument", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("IllegalStateException 처리")
    @Test
    public void handleIllegalStateExceptionTest() {
        // given
        IllegalStateException ex = mock(IllegalStateException.class);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleIllegalStateException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Not available resource", errorResponse.getMessage());
    }

    @DisplayName("요청 간 InvalidFormatException 처리")
    @Test
    public void handleInvalidFormatExceptionOnRequestTest() {
        // given
        InvalidFormatException ifx = new InvalidFormatException(null, "Invalid format", "value", Integer.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", ifx, inputMessage);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Value cannot be deserialized to expected type", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("요청 간 UnrecognizedPropertyException 처리")
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Body has property that target class do not know", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("요청 간 JsonMappingException 처리")
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Mapping to body and Java object failed", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("요청 간 JsonParseException 처리")
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Parsing body and Java object failed", errorResponse.getMessage());
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Malformed request body", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("응답 간 InvalidFormatException 처리")
    @Test
    public void handleInvalidFormatExceptionOnResponseTest() {
        // given
        InvalidFormatException ifx = new InvalidFormatException(null, "Invalid format", "value", Integer.class);
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException(outputMessage.toString(), ifx);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Value cannot be deserialized to expected type", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("응답 간 UnrecognizedPropertyException 처리")
    @Test
    void handleUnrecognizedPropertyExceptionOnResponseTest() {
        // given
        UnrecognizedPropertyException upx = new UnrecognizedPropertyException(null, null, null, null, null, null);
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException(outputMessage.toString(), upx);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Body has property that target class do not know", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("응답 간 JsonMappingException 처리")
    @Test
    void handleJsonMappingExceptionOnResponseTest() {
        // given
        JsonMappingException jmx = mock(JsonMappingException.class);
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException(outputMessage.toString(), jmx);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Mapping to body and Java object failed", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("응답 간 JsonParseException 처리")
    @Test
    void handleJsonParseExceptionOnResponseTest() {
        // given
        JsonParseException jpx = mock(JsonParseException.class);
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException(outputMessage.toString(), jpx);

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Parsing body and Java object failed", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @DisplayName("HttpMessageNotWritableException 처리")
    @Test
    public void handleHttpMessageNotWritableExceptionTest() {
        // given
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException("", mock(HttpMessageNotWritableException.class));

        // when
        ResponseEntity<DataResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotWritableException(ex);
        DataResponse<Void> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Malformed request body", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }
}
