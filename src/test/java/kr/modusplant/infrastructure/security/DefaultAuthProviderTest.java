package kr.modusplant.infrastructure.security;

import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.AccountStateException;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import kr.modusplant.infrastructure.security.exception.BannedException;
import kr.modusplant.infrastructure.security.exception.InactiveException;
import kr.modusplant.infrastructure.security.models.DefaultAuthToken;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class DefaultAuthProviderTest {

    private final DefaultUserDetailsService defaultUserDetailsService = Mockito.mock(DefaultUserDetailsService.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final DefaultAuthProvider authProvider = new DefaultAuthProvider(defaultUserDetailsService, passwordEncoder);

    private DefaultUserDetails.DefaultUserDetailsBuilder freshUserDetailsBuilder() {
        return DefaultUserDetails.builder()
                .email("test123@example.com")
                .password("encodedPw")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("유효한 자격 증명으로 인증된 토큰 반환")
    void testAuthenticate_givenValidCredentials_willReturnDefaultAuthToken() {
        // given
        DefaultUserDetails userDetails = freshUserDetailsBuilder()
                .provider(AuthProvider.BASIC).isActive(true).isBanned(false).build();
        Authentication requestToken = new DefaultAuthToken(userDetails.getEmail(), "userPw2!");
        given(defaultUserDetailsService.loadUserByUsername(userDetails.getEmail())).willReturn(userDetails);
        given(passwordEncoder.matches("userPw2!", userDetails.getPassword())).willReturn(true);

        // when
        Authentication result = authProvider.authenticate(requestToken);

        // then
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 예외 반환")
    void testAuthenticate_givenWrongPassword_willThrowException() {
        // given
        DefaultUserDetails userDetails = freshUserDetailsBuilder()
                .provider(AuthProvider.BASIC).isActive(true).isBanned(false).build();
        Authentication requestToken = new DefaultAuthToken(userDetails.getEmail(), "wrongPw1!");
        given(defaultUserDetailsService.loadUserByUsername(userDetails.getEmail())).willReturn(userDetails);
        given(passwordEncoder.matches("wrongPw1!", userDetails.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authProvider.authenticate(requestToken))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BAD_PASSWORD);
    }

    @Test
    @DisplayName("Google 계정과 연동된 사용자로 예외 반환")
    void testAuthenticate_givenGoogleLinkedAccount_willThrowException() {
        // given
        DefaultUserDetails userDetails = freshUserDetailsBuilder()
                .provider(AuthProvider.BASIC_GOOGLE).isActive(true).isBanned(false).build();
        Authentication requestToken = new DefaultAuthToken(userDetails.getEmail(), "userPw2!");
        given(defaultUserDetailsService.loadUserByUsername(userDetails.getEmail())).willReturn(userDetails);
        given(passwordEncoder.matches("userPw2!", userDetails.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authProvider.authenticate(requestToken))
                .isInstanceOf(AccountStateException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.FORBIDDEN_GOOGLE_LINKED_ACCOUNT);
    }

    @Test
    @DisplayName("Kakao 계정과 연동된 사용자로 예외 반환")
    void testAuthenticate_givenKakaoLinkedAccount_willThrowException() {
        // given
        DefaultUserDetails userDetails = freshUserDetailsBuilder()
                .provider(AuthProvider.BASIC_KAKAO).isActive(true).isBanned(false).build();
        Authentication requestToken = new DefaultAuthToken(userDetails.getEmail(), "userPw2!");
        given(defaultUserDetailsService.loadUserByUsername(userDetails.getEmail())).willReturn(userDetails);
        given(passwordEncoder.matches("userPw2!", userDetails.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authProvider.authenticate(requestToken))
                .isInstanceOf(AccountStateException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.FORBIDDEN_KAKAO_LINKED_ACCOUNT);
    }

    @Test
    @DisplayName("밴 처리된 계정으로 예외 반환")
    void testAuthenticate_givenBannedAccount_willThrowException() {
        // given
        DefaultUserDetails userDetails = freshUserDetailsBuilder()
                .provider(AuthProvider.BASIC).isActive(true).isBanned(true).build();
        Authentication requestToken = new DefaultAuthToken(userDetails.getEmail(), "userPw2!");
        given(defaultUserDetailsService.loadUserByUsername(userDetails.getEmail())).willReturn(userDetails);
        given(passwordEncoder.matches("userPw2!", userDetails.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authProvider.authenticate(requestToken))
                .isInstanceOf(BannedException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BANNED);
    }

    @Test
    @DisplayName("비활성화된 계정으로 예외 반환")
    void testAuthenticate_givenInactiveAccount_willThrowException() {
        // given
        DefaultUserDetails userDetails = freshUserDetailsBuilder()
                .provider(AuthProvider.BASIC).isActive(false).isBanned(false).build();
        Authentication requestToken = new DefaultAuthToken(userDetails.getEmail(), "userPw2!");
        given(defaultUserDetailsService.loadUserByUsername(userDetails.getEmail())).willReturn(userDetails);
        given(passwordEncoder.matches("userPw2!", userDetails.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authProvider.authenticate(requestToken))
                .isInstanceOf(InactiveException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.INACTIVE);
    }

    @Test
    @DisplayName("DefaultAuthToken 클래스로 지원 여부 확인 시 true 반환")
    void testSupports_givenDefaultAuthTokenClass_willReturnTrue() {
        // given & when & then
        assertThat(authProvider.supports(DefaultAuthToken.class)).isTrue();
    }

    @Test
    @DisplayName("지원하지 않는 클래스로 지원 여부 확인 시 false 반환")
    void testSupports_givenUnsupportedClass_willReturnFalse() {
        // given & when & then
        assertThat(authProvider.supports(UsernamePasswordAuthenticationToken.class)).isFalse();
    }
}
