package kr.modusplant.domains.identity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.adapter.EmailAuthTokenHelper;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.Password;
import kr.modusplant.domains.identity.normal.usecase.enums.EmailType;
import kr.modusplant.domains.identity.normal.usecase.port.contract.CallEmailSendApiGateway;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.identity.normal.usecase.request.EmailAuthRequest;
import kr.modusplant.domains.identity.normal.usecase.request.EmailValidationRequest;
import kr.modusplant.domains.identity.normal.usecase.request.InputValidationRequest;
import kr.modusplant.framework.redis.RedisHelper;
import kr.modusplant.framework.redis.RedisKeys;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.InvalidDataException;
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
public class EmailAuthController {

    private final EmailAuthTokenHelper tokenHelper;
    private final RedisHelper redisHelper;
    private final CallEmailSendApiGateway apiGateway;
    private final NormalIdentityRepository identityRepository;
    private final NormalIdentityUpdateRepository identityUpdateRepository;

    public String sendAuthCodeEmail(EmailAuthRequest request) {
        String verificationCode = tokenHelper.generateVerifyCode();
        apiGateway.execute(request.email(), verificationCode, EmailType.AUTHENTICATION_CODE_EMAIL);
        return tokenHelper.generateAuthCodeAccessToken(request.email(), verificationCode);
    }

    public void verifyAuthCodeEmail(EmailValidationRequest request, String accessToken) {
        tokenHelper.validateAuthCodeAccessToken(request, accessToken);
    }

    public String sendResetPasswordEmail(EmailAuthRequest request) {
        String email = request.email();

        if (!identityRepository.existsByEmailAndProvider(email, AuthProvider.BASIC.getValue())) {
            throw new EntityNotFoundException(NormalIdentityErrorCode.MEMBER_NOT_FOUND_WITH_EMAIL, "email");
        }

        UUID uuid = UUID.randomUUID();
        String stringUuid = String.valueOf(uuid);
        String redisKey = RedisKeys.generateRedisKey(RESET_PASSWORD_PREFIX, stringUuid);
        redisHelper.setString(redisKey, email, Duration.ofMinutes(3));

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
        identityUpdateRepository.updatePassword(Email.create(email), Password.create(password));
    }
}
