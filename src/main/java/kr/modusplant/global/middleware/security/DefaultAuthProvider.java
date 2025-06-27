package kr.modusplant.global.middleware.security;

import kr.modusplant.global.middleware.security.error.BannedException;
import kr.modusplant.global.middleware.security.error.DeletedException;
import kr.modusplant.global.middleware.security.error.DisabledByLinkingException;
import kr.modusplant.global.middleware.security.error.InactiveException;
import kr.modusplant.global.middleware.security.models.DefaultAuthToken;
import kr.modusplant.global.middleware.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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
            throw new BadCredentialsException("비밀번호가 틀렸습니다."); }
        if (userDetails.isDisabledByLinking()) {
            throw new DisabledByLinkingException("계정 연동으로 인해 이 계정을 사용할 수 없습니다."); }
        if (userDetails.isBanned()) {
            throw new BannedException("계정이 밴 되어 사용할 수 없습니다."); }
        if (userDetails.isDeleted()) {
            throw new DeletedException("계정이 삭제되어 더 이상 사용할 수 없습니다."); }
        if (!userDetails.isActive()) {
            throw new InactiveException("계정이 사용할 수 없습니다."); }

        return true;
    }
}
