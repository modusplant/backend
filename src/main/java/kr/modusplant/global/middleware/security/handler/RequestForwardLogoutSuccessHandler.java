package kr.modusplant.global.middleware.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class RequestForwardLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        response.setStatus(200);
        response.getWriter().write(
                objectMapper.writeValueAsString(DataResponse.ok())
        );
    }
}
