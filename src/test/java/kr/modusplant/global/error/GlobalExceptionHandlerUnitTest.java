package kr.modusplant.global.error;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUnitTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void handleRuntimeException_givenValidCondition_thenReturnMap() {
        // given
        RuntimeException ex = mock(RuntimeException.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // when
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(servletRequest, ex);
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        Map<String, Object> metadata = objectMapper.convertValue(responseBody.get("metaData"), new TypeReference<>() {});

        // then
        assertInstanceOf(Map.class, metadata);
        assertEquals(HttpStatus.BAD_REQUEST.value(), metadata.get("status"));
        assertNotNull(metadata.get("message"));

    }

    @Test
    public void handleGenericException_givenValidCondition_thenReturnMap() {
        // given
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        Exception ex = mock(Exception.class);

        // when
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(servletRequest, ex);
        Map<String, Object> errorResponse = response.getBody();

        // then
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.get("status"));
        assertNotNull(errorResponse.get("message"));
    }

    @Test
    public void handleIllegalArgumentException_givenValidCondition_thenReturnProblemDetail() {
        // given
        IllegalArgumentException ex = mock(IllegalArgumentException.class);

        // when
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleIllegalArgumentException(ex);
        ProblemDetail problemDetail = response.getBody();

        // then
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid method argument", problemDetail.getTitle());
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
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getMetadata().status());
        assertEquals("resource is not available", errorResponse.getMetadata().message());
    }

    @Test
    public void handleValidationException_givenValidCondition_thenReturnProblemDetail() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrorList = new ArrayList<>();
        fieldErrorList.add(new FieldError("SiteMemberEntity", "isActive", "isActive must not be null"));
        fieldErrorList.add(new FieldError("SiteMemberEntity", "nickname", "nickname must not be null"));

        given(ex.getBindingResult()).willReturn(bindingResult);
        given(bindingResult.getFieldErrors()).willReturn(fieldErrorList);

        // when
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleValidationException(ex);
        ProblemDetail problemDetail = response.getBody();

        // then
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid client data", problemDetail.getTitle());
        assertEquals("required property missing, invalid format, constraint violation, etc", problemDetail.getDetail());
    }

    @Test
    public void handleValidationException_givenEmptyFieldErrors_thenReturnProblemDetail() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        given(ex.getBindingResult()).willReturn(bindingResult);
        given(bindingResult.getFieldErrors()).willReturn(List.of());

        // when
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleValidationException(ex);
        ProblemDetail problemDetail = response.getBody();

        // then
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
    }

    @Test
    public void handleMalformedJsonException_givenValidCondition_thenReturnProblemDetail() {
        // given
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        // when
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleMalformedJsonException(ex);
        ProblemDetail problemDetail = response.getBody();

        // then
        assertNotNull(problemDetail);
        assertEquals("Invalid body format", problemDetail.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
    }

    @Test
    public void handleMalformedJsonException_givenInvalidFormatException_thenReturnProblemDetail() {
        // given
        InvalidFormatException ifx = new InvalidFormatException(null, "Invalid format", "value", Integer.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", ifx, inputMessage);

        // when
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleMalformedJsonException(ex);
        ProblemDetail problemDetail = response.getBody();

        // then
        assertNotNull(problemDetail);
        assertNotNull(Objects.requireNonNull(problemDetail.getProperties()).get("error path"));
        assertEquals("value cannot be deserialized to expected type", problemDetail.getDetail());
    }

    @Test void handleMalformedJsonException_givenUnrecognizedPropertyException_thenReturnProblemDetail() {
        // given
        UnrecognizedPropertyException upx = new UnrecognizedPropertyException(null, null, null, null, null, null);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", upx, inputMessage);

        // when
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleMalformedJsonException(ex);
        ProblemDetail problemDetail = response.getBody();

        // then
        assertNotNull(problemDetail);
        assertEquals("body has property that target class do not know", problemDetail.getDetail());
    }
}
