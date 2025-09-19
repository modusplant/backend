package kr.modusplant.infrastructure.security;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.infrastructure.security.error.*;
import kr.modusplant.infrastructure.security.models.DefaultAuthToken;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class DefaultAuthProvider implements AuthenticationProvider {

    private final DefaultUserDetailsService defaultUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        DefaultUserDetails userDetails = defaultUserDetailsService.loadUserByUsername(email);

        if(validateDefaultUserDetails(userDetails, password)) {
            return new DefaultAuthToken(userDetails, userDetails.getAuthorities());
        } else {
            return new DefaultAuthToken(email, password);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DefaultAuthToken.class.isAssignableFrom(authentication);
    }

    private boolean validateDefaultUserDetails(DefaultUserDetails userDetails, String password) {
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialException(IdentityErrorCode.BAD_PASSWORD); }
        if (userDetails.isDisabledByLinking()) {
            throw new DisabledByLinkingException(); }
        if (userDetails.isBanned()) {
            throw new BannedException(); }
        if (userDetails.isDeleted()) {
            throw new DeletedException(); }
        if (!userDetails.isActive()) {
            throw new InactiveException(); }

        return true;
    }
}
