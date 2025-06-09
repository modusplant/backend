package kr.modusplant.global.middleware.security;

import kr.modusplant.global.middleware.security.error.BannedException;
import kr.modusplant.global.middleware.security.error.DeletedException;
import kr.modusplant.global.middleware.security.error.DisabledByLinkingException;
import kr.modusplant.global.middleware.security.error.InavtiveException;
import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class SiteMemberAuthProvider implements AuthenticationProvider {

    private final SiteMemberUserDetailsService memberUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        SiteMemberUserDetails userDetails = memberUserDetailsService.loadUserByUsername(email);

        if(validateSiteMemberUserDetails(userDetails, password)) {
            return new SiteMemberAuthToken(userDetails, userDetails.getAuthorities());
        } else {
            return new SiteMemberAuthToken(email, password);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SiteMemberAuthToken.class.isAssignableFrom(authentication);
    }

    private boolean validateSiteMemberUserDetails(SiteMemberUserDetails userDetails, String password) {
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("The password is not correct");
        } else if (!userDetails.isActive()) {
            throw new InavtiveException("The account is inactive");
        } else if (userDetails.isDisabledByLinking()) {
            throw new DisabledByLinkingException("The account is disabled by account linking");
        } else if (userDetails.isBanned()) {
            throw new BannedException("The account is banned");
        } else if (userDetails.isDeleted()) {
            throw new DeletedException("The account is deleted");
        } else {
            return true;
        }
    }
}
