package kr.modusplant.global.middleware.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.modules.auth.normal.login.app.http.request.NormalLoginRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import java.io.IOException;

public class NormalLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final AuthenticationManager authManager;

    public NormalLoginFilter(
            ObjectMapper objectMapper,
            Validator validator,
            AuthenticationManager authManager) {
        super(new AntPathRequestMatcher("/api/auth/login", HttpMethod.POST.name()));
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.authManager = authManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        NormalLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), NormalLoginRequest.class);
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(loginRequest, "loginRequest");
        validator.validate(loginRequest, result);

        if (result.hasErrors()) {
            throw new IllegalArgumentException(result.getFieldError().getDefaultMessage());
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
