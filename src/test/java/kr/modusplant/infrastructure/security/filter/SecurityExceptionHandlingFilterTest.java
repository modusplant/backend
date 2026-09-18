package kr.modusplant.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.security.DefaultAuthenticationEntryPoint;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BannedException;
import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

class SecurityExceptionHandlingFilterTest {

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final DefaultAuthenticationEntryPoint entryPoint = Mockito.mock(DefaultAuthenticationEntryPoint.class);
    private final SecurityExceptionHandlingFilter filter = new SecurityExceptionHandlingFilter(objectMapper, entryPoint);

    @Test
    @DisplayName("예외 없이 필터 체인 진행 활동 수행")
    void testDoFilterInternal_givenNoException_willProceedChain() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("인증 예외 발생 시 엔트리 포인트로 위임 활동 수행")
    void testDoFilterInternal_givenAuthenticationException_willDelegateToEntryPoint() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        BannedException exception = new BannedException();
        willThrow(exception).given(filterChain).doFilter(request, response);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(entryPoint).should().commence(request, response, exception);
    }

    @Test
    @DisplayName("비즈니스 예외 발생 시 해당 에러 코드로 응답 작성 활동 수행")
    void testDoFilterInternal_givenBusinessException_willWriteBusinessErrorResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        BusinessException exception = new BusinessException(GeneralErrorCode.EMPTY_VALUE);
        willThrow(exception).given(filterChain).doFilter(request, response);
        given(objectMapper.writeValueAsString(any())).willReturn("{\"code\":\"empty_value\"}");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        Mockito.verify(response).setStatus(GeneralErrorCode.EMPTY_VALUE.getHttpStatus());
    }

    @Test
    @DisplayName("알 수 없는 예외 발생 시 인증 실패 응답 작성 활동 수행")
    void testDoFilterInternal_givenUnknownException_willWriteAuthenticationFailedResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        willThrow(new RuntimeException("boom")).given(filterChain).doFilter(request, response);
        given(objectMapper.writeValueAsString(any())).willReturn("{\"code\":\"authentication_failed\"}");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        Mockito.verify(response).setStatus(SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus());
    }
}
