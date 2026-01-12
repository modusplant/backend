package kr.modusplant.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import kr.modusplant.infrastructure.security.models.DefaultAuthToken;
import kr.modusplant.infrastructure.security.models.NormalLoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class EmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final AuthenticationManager authManager;

    public EmailPasswordAuthenticationFilter(
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
          String errorFieldName = Optional.ofNullable(result.getFieldError())
                    .map(FieldError::getField)
                    .orElse(null);

            if (errorFieldName != null && errorFieldName.equals("password")) {
                throw new BadCredentialException(SecurityErrorCode.BAD_PASSWORD_FORMAT);
            }
            if (errorFieldName != null && errorFieldName.equals("email")) {
                throw new BadCredentialException(SecurityErrorCode.BAD_EMAIL_FORMAT);
            }
            throw new BadCredentialException(SecurityErrorCode.AUTHENTICATION_FAILED);
        }

        DefaultAuthToken requestToken = new DefaultAuthToken(
                loginRequest.email(), loginRequest.password()
        );

        return authManager.authenticate(requestToken);
    }
}
