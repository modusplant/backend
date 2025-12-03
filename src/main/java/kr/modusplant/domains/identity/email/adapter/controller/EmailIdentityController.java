package kr.modusplant.domains.identity.email.adapter.controller;

import kr.modusplant.domains.identity.email.adapter.EmailIdentityTokenHelper;
import kr.modusplant.domains.identity.email.domain.exception.enums.EmailIdentityErrorCode;
import kr.modusplant.domains.identity.email.domain.vo.Password;
import kr.modusplant.domains.identity.email.usecase.enums.EmailType;
import kr.modusplant.domains.identity.email.usecase.port.gateway.CallEmailSendApiGateway;
import kr.modusplant.domains.identity.email.usecase.port.repository.EmailIdentityRepository;
import kr.modusplant.domains.identity.email.usecase.request.EmailIdentityRequest;
import kr.modusplant.domains.identity.email.usecase.request.EmailValidationRequest;
import kr.modusplant.domains.identity.email.usecase.request.InputValidationRequest;
import kr.modusplant.framework.redis.RedisHelper;
import kr.modusplant.framework.redis.RedisKeys;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.kernel.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode.INVALID_ID;
import static kr.modusplant.framework.redis.RedisKeys.RESET_PASSWORD_PREFIX;
import static kr.modusplant.infrastructure.jwt.enums.TokenScope.RESET_PASSWORD_EMAIL;
import static kr.modusplant.infrastructure.jwt.enums.TokenScope.RESET_PASSWORD_INPUT;

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

    public String sendResetPasswordEmail(EmailIdentityRequest request) {
        String email = request.email();

        if (!repository.existsByEmailAndProvider(Email.create(email))) {
            throw new EntityNotFoundException(EmailIdentityErrorCode.MEMBER_NOT_FOUND_WITH_EMAIL, "email");
        }

        String stringUuid = String.valueOf(UUID.randomUUID());
        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, stringUuid);
        redisHelper.setString(redisKey, email, Duration.ofMinutes(5));

        apiGateway.execute(email, stringUuid, EmailType.RESET_PASSWORD_EMAIL);
        return tokenHelper.generateResetPasswordAccessToken(email, RESET_PASSWORD_EMAIL);
    }

    public String verifyResetPasswordEmail(UUID uuid, String accessToken) {
        String stringUuid = String.valueOf(uuid);
        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, stringUuid);
        String storedEmail = redisHelper.getString(redisKey).orElseThrow(() -> new InvalidDataException(INVALID_ID, "uuid"));
        tokenHelper.validateResetPasswordEmailAccessToken(storedEmail, accessToken);
        return tokenHelper.generateResetPasswordAccessToken(storedEmail, RESET_PASSWORD_INPUT);
    }

    public void verifyResetPasswordInput(InputValidationRequest request, String accessToken) {
        tokenHelper.validateResetPasswordInputAccessToken(accessToken);
        String email = tokenHelper.getClaims(accessToken).get("email", String.class);
        String password = request.password();
        repository.updatePassword(Email.create(email), Password.create(password));
    }
}
