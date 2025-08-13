package kr.modusplant.global.middleware.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.outbound.jackson.http.response.DataResponse;
import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
import kr.modusplant.global.middleware.security.error.BusinessAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class WriteResponseLoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        if(exception instanceof BusinessAuthenticationException ex) {
            response.setStatus(ex.getErrorCode().getHttpStatus().getValue());
            response.getWriter().write(
                    objectMapper.writeValueAsString(DataResponse
                            .of(ex.getErrorCode())
                    )
            );
        } else {
            response.setStatus(SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus().getValue());
            response.getWriter().write(
                    objectMapper.writeValueAsString(DataResponse
                            .of(SecurityErrorCode.AUTHENTICATION_FAILED)
                    )
            );
        }
    }
}
