package kr.modusplant.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import kr.modusplant.infrastructure.security.models.NormalLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willDoNothing;

class EmailPasswordAuthenticationFilterTest {

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final Validator validator = Mockito.mock(Validator.class);
    private final AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
    private final EmailPasswordAuthenticationFilter filter =
            new EmailPasswordAuthenticationFilter(objectMapper, validator, authManager);

    private final NormalLoginRequest testLoginRequest = new NormalLoginRequest("test123@example.com", "userPw2!");

    @Test
    @DisplayName("유효한 로그인 요청으로 인증 관리자에게 위임 후 인증 정보 반환")
    void testAttemptAuthentication_givenValidLoginRequest_willReturnAuthentication() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        given(request.getInputStream()).willReturn(Mockito.mock(ServletInputStream.class));
        given(objectMapper.readValue(any(ServletInputStream.class), eq(NormalLoginRequest.class)))
                .willReturn(testLoginRequest);
        willDoNothing().given(validator).validate(any(), any(Errors.class));
        Authentication expected = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        given(authManager.authenticate(any())).willReturn(expected);

        // when
        Authentication result = filter.attemptAuthentication(request, response);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("비밀번호 형식 오류로 예외 반환")
    void testAttemptAuthentication_givenInvalidPasswordFormat_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        stubValidRequestBody(request);
        willAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("password", "bad_password_format");
            return null;
        }).given(validator).validate(any(), any(Errors.class));

        // when & then
        assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BAD_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("이메일 형식 오류로 예외 반환")
    void testAttemptAuthentication_givenInvalidEmailFormat_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        stubValidRequestBody(request);
        willAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("email", "bad_email_format");
            return null;
        }).given(validator).validate(any(), any(Errors.class));

        // when & then
        assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BAD_EMAIL_FORMAT);
    }

    private void stubValidRequestBody(HttpServletRequest request) {
        try {
            given(request.getInputStream()).willReturn(Mockito.mock(ServletInputStream.class));
            given(objectMapper.readValue(any(ServletInputStream.class), eq(NormalLoginRequest.class)))
                    .willReturn(testLoginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
