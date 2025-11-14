package kr.modusplant.domains.normalidentity.normal.adapter.controller;

import kr.modusplant.domains.normalidentity.normal.adapter.EmailAuthTokenHelper;
import kr.modusplant.domains.normalidentity.normal.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.domains.normalidentity.normal.exception.DataAlreadyExistsException;
import kr.modusplant.domains.normalidentity.normal.usecase.enums.EmailType;
import kr.modusplant.domains.normalidentity.normal.usecase.port.contract.CallEmailSendApiGateway;
import kr.modusplant.domains.normalidentity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.normalidentity.normal.usecase.request.EmailAuthRequest;
import kr.modusplant.domains.normalidentity.normal.usecase.request.EmailValidationRequest;
import kr.modusplant.framework.out.redis.RedisHelper;
import kr.modusplant.framework.out.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static kr.modusplant.framework.out.redis.RedisKeys.RESET_PASSWORD_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailAuthController {

    private final EmailAuthTokenHelper tokenHelper;
    private final RedisHelper redisHelper;
    private final CallEmailSendApiGateway apiGateway;
    private final NormalIdentityRepository identityRepository;

    public String sendAuthEmail(EmailAuthRequest request) {
        String verificationCode = tokenHelper.generateVerifyCode();
        apiGateway.execute(request.email(), verificationCode, EmailType.SIGNUP_VERIFY_EMAIL);
        return tokenHelper.generateVerifyAccessToken(request.email(), verificationCode);
    }

    public void verifyAuthEmailCode(EmailValidationRequest request, String accessToken) {
        tokenHelper.validateVerifyAccessToken(request, accessToken);
    }

    public void sendResetPasswordCode(EmailAuthRequest request) {
        String email = request.email();

        if(identityRepository.existsByEmailAndProvider(email, "Basic")) {
            throw new DataAlreadyExistsException(IdentityErrorCode.ALREADY_EXISTS_MEMBER);
        }

        String verifyCode = tokenHelper.generateVerifyCode();

        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, email);
        redisHelper.setString(redisKey, verifyCode, Duration.ofMinutes(5));

        apiGateway.execute(email, verifyCode, EmailType.RESET_PASSWORD_EMAIL);
    }

    public void verifyResetPasswordCode(EmailValidationRequest request) {
        String email = request.email();

        if(identityRepository.existsByEmailAndProvider(email, "Basic")) {
            throw new DataAlreadyExistsException(IdentityErrorCode.ALREADY_EXISTS_MEMBER);
        }

        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, email);
        String storedCode = redisHelper.getString(redisKey).orElseThrow(() -> new RuntimeException("코드를 잘못 입력하였습니다."));
        if (!storedCode.equals(request.verifyCode())) {
            throw new RuntimeException("코드를 잘못 입력하였습니다.");
        }
    }
}
