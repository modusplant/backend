package kr.modusplant.domains.identity.framework.legacy.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.domains.identity.framework.legacy.error.BusinessAuthenticationException;
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
            response.setStatus(IdentityErrorCode.AUTHENTICATION_FAILED.getHttpStatus().getValue());
            response.getWriter().write(
                    objectMapper.writeValueAsString(DataResponse
                            .of(IdentityErrorCode.AUTHENTICATION_FAILED)
                    )
            );
        }
    }
}
