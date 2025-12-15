package kr.modusplant.domains.account.email.adapter.controller;

import kr.modusplant.domains.account.email.adapter.EmailIdentityTokenHelper;
import kr.modusplant.domains.account.email.domain.exception.enums.EmailIdentityErrorCode;
import kr.modusplant.domains.account.email.usecase.enums.EmailType;
import kr.modusplant.domains.account.email.usecase.port.gateway.CallEmailSendApiGateway;
import kr.modusplant.domains.account.email.usecase.port.repository.EmailIdentityRepository;
import kr.modusplant.domains.account.email.usecase.request.EmailIdentityRequest;
import kr.modusplant.domains.account.email.usecase.request.EmailValidationRequest;
import kr.modusplant.domains.account.email.usecase.request.InputValidationRequest;
import kr.modusplant.domains.account.normal.domain.exception.InvalidValueException;
import kr.modusplant.framework.redis.RedisHelper;
import kr.modusplant.framework.redis.RedisKeys;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static kr.modusplant.framework.redis.RedisKeys.RESET_PASSWORD_PREFIX;
import static kr.modusplant.infrastructure.jwt.enums.TokenScope.RESET_PASSWORD_INPUT;
import static kr.modusplant.shared.exception.enums.ErrorCode.PASSWORD_RESET_EMAIL_VERIFY_FAIL;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailIdentityController {

    private final EmailIdentityTokenHelper tokenHelper;
    private final RedisHelper redisHelper;
    private final CallEmailSendApiGateway apiGateway;
    private final EmailIdentityRepository repository;

    public String sendAuthCodeEmail(EmailIdentityRequest request) {
        String verificationCode = tokenHelper.generateVerifyCode();
        apiGateway.execute(request.email(), verificationCode, EmailType.AUTHENTICATION_CODE_EMAIL);
        return tokenHelper.generateAuthCodeAccessToken(request.email(), verificationCode);
    }

    public void verifyAuthCodeEmail(EmailValidationRequest request, String accessToken) {
        tokenHelper.validateAuthCodeAccessToken(request, accessToken);
    }

    public void sendResetPasswordEmail(EmailIdentityRequest request) {
        String email = request.email();

        if (!repository.existsByEmailAndProvider(Email.create(email))) {
            throw new EntityNotFoundException(EmailIdentityErrorCode.MEMBER_NOT_FOUND_WITH_EMAIL, "email");
        }

        UUID uuid = UUID.randomUUID();
        String stringUuid = String.valueOf(uuid);
        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, stringUuid);
        redisHelper.setString(redisKey, email, Duration.ofMinutes(5));

        apiGateway.execute(email, stringUuid, EmailType.RESET_PASSWORD_EMAIL);
    }

    /**
     * 비밀번호 재설정 토큰(UUID) 검증
     * - 쿠키/JWT 의존성 없이 UUID 검증
     * - 프론트엔드에서 호출하여 토큰 유효성 확인 후 비밀번호 재설정 진행
     */
    public String verifyResetPasswordEmail(UUID uuid) {
        String stringUuid = String.valueOf(uuid);
        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, stringUuid);
        String storedEmail = redisHelper.getString(redisKey)
                .orElseThrow(() -> new InvalidValueException(PASSWORD_RESET_EMAIL_VERIFY_FAIL));
        return tokenHelper.generateResetPasswordAccessToken(storedEmail, stringUuid, RESET_PASSWORD_INPUT);
    }

    public void verifyResetPasswordInput(InputValidationRequest request, String accessToken) {
        tokenHelper.validateResetPasswordInputAccessToken(accessToken);
        String email = tokenHelper.getClaims(accessToken).get("email", String.class);
        String uuid = tokenHelper.getClaims(accessToken).get("uuid", String.class);
        String password = request.password();
        repository.updatePassword(Email.create(email), Password.create(password));
        redisHelper.delete(RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, uuid));
    }
}
