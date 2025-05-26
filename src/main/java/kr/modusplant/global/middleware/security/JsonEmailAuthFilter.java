package kr.modusplant.global.middleware.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.modules.auth.normal.login.app.http.NormalLoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

public class JsonEmailAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authManager;

    public JsonEmailAuthFilter(
            ObjectMapper objectMapper,
            AuthenticationManager authManager) {
        super("/api/auth/login");
        this.objectMapper = objectMapper;
        this.authManager = authManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        NormalLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), NormalLoginRequest.class);
        System.out.println("The arrived request" + loginRequest);

        if (!loginRequest.checkFieldValidation()) {
            throw new IllegalArgumentException("one of email password deviceId missing");
        }

        SiteMemberAuthToken requestToken = new SiteMemberAuthToken(
                loginRequest.email(), loginRequest.password()
        );

        Authentication authentication = authManager.authenticate(requestToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.setAttribute("authentication", authentication);

        return authentication;
    }
}
