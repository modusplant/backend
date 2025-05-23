package kr.modusplant.modules.auth.email.service;

import kr.modusplant.modules.auth.email.model.request.EmailRequest;
import kr.modusplant.modules.auth.email.model.request.VerifyEmailRequest;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final TokenProvider tokenProvider;
    private final MailService mailService;

    public String sendVerifyEmail(EmailRequest request) {
        String email = request.getEmail();
        // 이메일 인증코드 생성
        String verifyCode = tokenProvider.generateVerifyCode();
        // JWT 토큰 생성
        String accessToken = tokenProvider.generateVerifyAccessToken(email, verifyCode);

        return accessToken;
    }

    public void verifyEmail(VerifyEmailRequest verifyEmailRequest, String accessToken) {
        tokenProvider.validateVerifyAccessToken(accessToken, verifyEmailRequest.getVerifyCode());
    }

    public void sendResetPasswordCode(EmailRequest request) {

    }

}
