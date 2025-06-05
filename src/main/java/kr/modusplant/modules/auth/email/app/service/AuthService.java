package kr.modusplant.modules.auth.email.app.service;

import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.global.middleware.redis.RedisHelper;
import kr.modusplant.global.middleware.redis.RedisKeys;
import kr.modusplant.modules.auth.email.app.http.request.EmailRequest;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static kr.modusplant.global.middleware.redis.RedisKeys.RESET_PASSWORD_PREFIX;
import static kr.modusplant.global.vo.CamelCaseWord.RESET_PASSWORD_EMAIL;
import static kr.modusplant.global.vo.CamelCaseWord.SIGNUP_VERIFY_EMAIL;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final TokenProvider tokenProvider;
    private final RedisHelper redisHelper;

    private final MailService mailService;
    private final SiteMemberAuthRepository siteMemberAuthRepository;

    public String sendVerifyEmail(EmailRequest request) {
        String email = request.getEmail();
        // 이메일 인증코드 생성
        String verifyCode = tokenProvider.generateVerifyCode();
        // JWT 토큰 생성
        String accessToken = tokenProvider.generateVerifyAccessToken(email, verifyCode);

        mailService.callSendEmail(email, verifyCode, SIGNUP_VERIFY_EMAIL);
        return accessToken;
    }

    public void verifyEmail(VerifyEmailRequest verifyEmailRequest, String accessToken) {
        tokenProvider.validateVerifyAccessToken(accessToken, verifyEmailRequest.getVerifyCode());
    }

    public void sendResetPasswordCode(EmailRequest request) {
        // 가입한 유저인지 확인
        String email = request.getEmail();
        validateEmailExists(email);

        // 이메일 인증코드 생성
        String verifyCode = tokenProvider.generateVerifyCode();

        // 인증코드 Redis 저장
        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, email);
        redisHelper.setString(redisKey, verifyCode, Duration.ofMinutes(5));

        // 인증코드 메일 발신
        mailService.callSendEmail(email, verifyCode, RESET_PASSWORD_EMAIL);
    }

    public void verifyResetPasswordCode(VerifyEmailRequest request) {
        String email = request.getEmail();
        validateEmailExists(email);

        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, email);
        String storedCode = redisHelper.getString(redisKey).orElseThrow(() -> new RuntimeException("verification code is invalid. Please try again"));
        if (!storedCode.equals(request.getVerifyCode())) {
            throw new RuntimeException("verification code is invalid. Please try again");
        }
    }

    private void validateEmailExists(String email) {
        if (!siteMemberAuthRepository.existsByEmail(email)) {
            throw new RuntimeException("Email not found");
        }
    }
}
