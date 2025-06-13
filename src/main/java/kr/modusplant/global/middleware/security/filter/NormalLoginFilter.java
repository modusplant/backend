package kr.modusplant.global.middleware.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.modules.auth.normal.login.app.http.NormalLoginRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class NormalLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authManager;

    public NormalLoginFilter(
            ObjectMapper objectMapper,
            AuthenticationManager authManager) {
        super(new AntPathRequestMatcher("/api/auth/login", HttpMethod.POST.name()));
        this.objectMapper = objectMapper;
        this.authManager = authManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        NormalLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), NormalLoginRequest.class);

        if (!loginRequest.checkFieldValidation()) {
            throw new IllegalArgumentException("missing email or password");
        }

        SiteMemberAuthToken requestToken = new SiteMemberAuthToken(
                loginRequest.email(), loginRequest.password()
        );

        Authentication authenticatedToken = authManager.authenticate(requestToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticatedToken);
        SecurityContextHolder.setContext(context);

        return authenticatedToken;
    }
}
