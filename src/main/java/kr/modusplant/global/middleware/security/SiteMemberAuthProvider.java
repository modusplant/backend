package kr.modusplant.global.middleware.security;

import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
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
        // TODO: 테스트가 완료된 후에 출력 코드를 지우고 인증 로직도 복구할 것.
        System.out.println("The email from authProvider: " + email);

        SiteMemberUserDetails userDetails = memberUserDetailsService.loadUserByUsername(email);

//        if(passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("The credentials are valid.");
//            return new SiteMemberAuthToken(userDetails, userDetails.getAuthorities());
//        } else {
//            System.out.println("The credential was invalid.");
//            return new SiteMemberAuthToken(email, password);
//        }
        System.out.println("The process of AuthenticationProvider has passed");
        return new SiteMemberAuthToken(userDetails, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SiteMemberAuthToken.class.isAssignableFrom(authentication);
    }
}
