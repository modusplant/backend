package kr.modusplant.global.middleware.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.global.enums.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(
                objectMapper.writeValueAsString(DataResponse
                        .of(HttpStatus.UNAUTHORIZED.value(), ResponseCode.UNAUTHORIZED.getValue(), "인증에 실패하였습니다.")
                )
        );
    }
}
