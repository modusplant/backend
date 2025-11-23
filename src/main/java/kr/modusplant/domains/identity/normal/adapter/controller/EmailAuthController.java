package kr.modusplant.domains.identity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.adapter.EmailAuthTokenHelper;
import kr.modusplant.domains.identity.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.usecase.enums.EmailType;
import kr.modusplant.domains.identity.normal.usecase.port.contract.CallEmailSendApiGateway;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.identity.normal.usecase.request.EmailAuthRequest;
import kr.modusplant.domains.identity.normal.usecase.request.EmailValidationRequest;
import kr.modusplant.framework.redis.RedisHelper;
import kr.modusplant.framework.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static kr.modusplant.framework.redis.RedisKeys.RESET_PASSWORD_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailAuthController {

    private final EmailAuthTokenHelper tokenHelper;
    private final RedisHelper redisHelper;
    private final CallEmailSendApiGateway apiGateway;
    private final NormalIdentityReadRepository readRepository;

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

        if(readRepository.existsByEmailAndProvider(Email.create(email))) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_MEMBER);
        }

        String verifyCode = tokenHelper.generateVerifyCode();

        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, email);
        redisHelper.setString(redisKey, verifyCode, Duration.ofMinutes(5));

        apiGateway.execute(email, verifyCode, EmailType.RESET_PASSWORD_EMAIL);
    }

    public void verifyResetPasswordCode(EmailValidationRequest request) {
        String email = request.email();

        if(readRepository.existsByEmailAndProvider(Email.create(email))) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_MEMBER);
        }

        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, email);
        String storedCode = redisHelper.getString(redisKey).orElseThrow(() -> new RuntimeException("코드를 잘못 입력하였습니다."));
        if (!storedCode.equals(request.verifyCode())) {
            throw new RuntimeException("코드를 잘못 입력하였습니다.");
        }
    }
}
