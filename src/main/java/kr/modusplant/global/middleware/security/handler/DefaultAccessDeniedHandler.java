package kr.modusplant.global.middleware.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class DefaultAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(SecurityErrorCode.ACCESS_DENIED.getHttpStatus().value());
        response.getWriter().write(
                objectMapper.writeValueAsString(DataResponse
                        .of(SecurityErrorCode.ACCESS_DENIED.getHttpStatus().value(),
                                SecurityErrorCode.ACCESS_DENIED.getCode(),
                                SecurityErrorCode.ACCESS_DENIED.getMessage())
                )
        );
    }
}
