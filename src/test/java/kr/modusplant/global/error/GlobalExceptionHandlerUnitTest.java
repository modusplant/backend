package kr.modusplant.global.error;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUnitTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

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
        assertEquals(URI.create("about:blank"), problemDetail.getType());
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
}
