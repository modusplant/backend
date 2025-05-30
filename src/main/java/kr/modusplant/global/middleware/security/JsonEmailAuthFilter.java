package kr.modusplant.global.middleware.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.modules.auth.normal.login.app.http.NormalLoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class JsonEmailAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authManager;

    public JsonEmailAuthFilter(
            ObjectMapper objectMapper,
            AuthenticationManager authManager) {
        super(new AntPathRequestMatcher("/api/auth/login", "POST"));
        this.objectMapper = objectMapper;
        this.authManager = authManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        NormalLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), NormalLoginRequest.class);
        System.out.println("The arrived request in JsonEmailAuthFilter" + loginRequest);

        if (!loginRequest.checkFieldValidation()) {
            throw new IllegalArgumentException("one of email password deviceId missing");
        }

        SiteMemberAuthToken requestToken = new SiteMemberAuthToken(
                loginRequest.email(), loginRequest.password()
        );

        Authentication authenticatedToken = authManager.authenticate(requestToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticatedToken);
        SecurityContextHolder.setContext(context);

        request.setAttribute("authentication", authenticatedToken);

        return authenticatedToken;
    }
}
