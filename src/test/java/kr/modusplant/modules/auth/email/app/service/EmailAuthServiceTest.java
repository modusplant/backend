package kr.modusplant.modules.auth.email.app.service;

import kr.modusplant.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.domains.member.error.SiteMemberAuthNotFoundException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.global.config.TestAopConfig;
import kr.modusplant.global.middleware.redis.RedisHelper;
import kr.modusplant.modules.auth.email.app.http.request.EmailRequest;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.auth.email.enums.EmailType;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

import static kr.modusplant.global.middleware.redis.RedisKeys.RESET_PASSWORD_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(classes = {TestAopConfig.class})
@Transactional
class EmailAuthServiceTest implements SiteMemberAuthEntityTestUtils {

    @Autowired
    private EmailAuthService emailAuthService;

    @Autowired
    private SiteMemberAuthRepository siteMemberAuthRepository;

    @MockitoBean
    private RedisHelper redisHelper;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private TokenProvider tokenProvider;

    private final String email = "test@example.com";
    private final String code = "123456";

    private void createTestUser() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberAuthRepository.save(
                createMemberAuthBasicUserEntityBuilder()
                        .activeMember(member)
                        .originalMember(member)
                        .email(email)
                        .build()
        );
    }

    @Test
    @DisplayName("회원가입 시 본인인증 메일전송 성공 테스트")
    void sendVerifyEmail_success() {
        // given
        EmailRequest request = new EmailRequest();
        setField(request, "email", email);

        when(tokenProvider.generateVerifyCode()).thenReturn(code);
        when(tokenProvider.generateVerifyAccessToken(email, code)).thenReturn("jwt-token");

        // when
        String result = emailAuthService.sendVerifyEmail(request);

        // then
        assertThat(result).isEqualTo("jwt-token");
        verify(tokenProvider).generateVerifyCode();
        verify(tokenProvider).generateVerifyAccessToken(email, code);
        verify(mailService).callSendEmail(eq(email), eq(code), eq(EmailType.SIGNUP_VERIFY_EMAIL));
    }

    @Test
    @DisplayName("이메일 인증코드 검증 성공 테스트")
    void verifyEmail_success() {
        // given
        String token = "mocked-jwt-token";
        VerifyEmailRequest request = new VerifyEmailRequest();
        setField(request, "email", email);
        setField(request, "verifyCode", code);

        // when
        emailAuthService.verifyEmail(request, token);

        // then
        verify(tokenProvider).validateVerifyAccessToken(token, request);
    }

    @Test
    @DisplayName("비밀번호 재설정 메일전송 성공 테스트")
    void sendResetPasswordCode_success() {
        // given
        createTestUser();
        EmailRequest request = new EmailRequest();
        setField(request, "email", email);

        when(tokenProvider.generateVerifyCode()).thenReturn(code);

        // when
        emailAuthService.sendResetPasswordCode(request);

        // then
        verify(redisHelper).setString(
                contains(RESET_PASSWORD_PREFIX), eq(code), eq(Duration.ofMinutes(5))
        );
        verify(mailService).callSendEmail(eq(email), eq(code), eq(EmailType.RESET_PASSWORD_EMAIL));
    }

    @Test
    @DisplayName("비밀번호 재설정 메일전송 실패 테스트(존재하지 않는 회원 이메일)")
    void sendResetPasswordCode_fail_whenEmailNotExists() {
        // given
        EmailRequest request = new EmailRequest();
        setField(request, "email", "notExistsEmail@gmail.com");

        // when/then
        assertThatThrownBy(() -> emailAuthService.sendResetPasswordCode(request))
                .isInstanceOf(SiteMemberAuthNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호 재설정 검증 성공 테스트")
    void verifyResetPasswordCode_success() {
        // given
        createTestUser();
        VerifyEmailRequest request = new VerifyEmailRequest();
        setField(request, "email", email);
        setField(request, "verifyCode", code);

        when(redisHelper.getString(contains(RESET_PASSWORD_PREFIX)))
                .thenReturn(Optional.of(code));

        // when
        emailAuthService.verifyResetPasswordCode(request);

        // then
        verify(redisHelper).getString(RESET_PASSWORD_PREFIX.concat(email));
    }

    @Test
    @DisplayName("비밀번호 재설정 검증 실패 테스트(레디스 인증코드 조회 실패)")
    void verifyResetPasswordCode_fail_whenCodeNotInRedis() {
        // given
        createTestUser();
        VerifyEmailRequest request = new VerifyEmailRequest();
        setField(request, "email", email);
        setField(request, "verifyCode", code);

        when(redisHelper.getString(anyString())).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> emailAuthService.verifyResetPasswordCode(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("코드를 잘못 입력하였습니다.");
    }

    @Test
    @DisplayName("비밀번호 재설정 검증 실패 테스트(레디스 인증코드와 불일치)")
    void verifyResetPasswordCode_fail_whenCodeDoesNotMatch() {
        // given
        createTestUser();
        VerifyEmailRequest request = new VerifyEmailRequest();
        setField(request, "email", email);
        setField(request, "verifyCode", "wrong-code");

        when(redisHelper.getString(anyString())).thenReturn(Optional.of(code));

        // expect
        assertThatThrownBy(() -> emailAuthService.verifyResetPasswordCode(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("코드를 잘못 입력하였습니다.");
    }
}
