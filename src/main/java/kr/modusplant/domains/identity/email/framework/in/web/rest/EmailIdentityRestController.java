package kr.modusplant.domains.identity.email.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.identity.email.adapter.controller.EmailIdentityController;
import kr.modusplant.domains.identity.email.usecase.request.EmailIdentityRequest;
import kr.modusplant.domains.identity.email.usecase.request.EmailValidationRequest;
import kr.modusplant.domains.identity.email.usecase.request.InputValidationRequest;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.REGEX_UUID;

@Tag(name = "이메일 API", description = "이메일 인증 메일 발송과 검증을 다루는 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class EmailIdentityRestController {

    private final EmailIdentityController controller;

    @Operation(summary = "인증 코드 메일 전송 API", description = "인증 코드를 포함하는 메일을 발송합니다. ")
    @PostMapping("/members/verify-email/send")
    public ResponseEntity<DataResponse<?>> sendAuthCodeEmail(
            @RequestBody @Valid EmailIdentityRequest request,
            HttpServletResponse httpResponse
    ) {
        String accessToken = controller.sendAuthCodeEmail(request);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "인증 코드 메일 검증 API", description = "인증을 위해 인증 코드를 검증합니다. ")
    @PostMapping("/members/verify-email")
    public ResponseEntity<DataResponse<?>> verifyAuthCodeEmail(
            @RequestBody @Valid EmailValidationRequest request,
            @Parameter(hidden = true)
            @NotNull(message = "인증 토큰이 비어 있습니다. ")
            @CookieValue(value = "Authorization", required = false) String accessToken
    ) {
        controller.verifyAuthCodeEmail(request, accessToken);

        return ResponseEntity.ok(
                DataResponse.ok(new HashMap<>() {{
                    put("hasEmailAuth", true);
                }}));
    }

    @Operation(summary = "비밀번호 재설정 메일 전송 API", description = "비밀번호 재설정 전용 하이퍼링크를 포함하는 메일을 발송합니다.")
    @PostMapping("/auth/reset-password-request/send")
    public ResponseEntity<DataResponse<?>> sendResetPasswordEmail(
            @RequestBody @Valid EmailIdentityRequest request
    ) {
        controller.sendResetPasswordEmail(request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "비밀번호 재설정 메일 검증 API", description = "UUID 토큰을 검증하고 비밀번호 재설정을 위한 인증 토큰을 발급합니다.")
    @PostMapping("/auth/reset-password-request/verify/email")
    public ResponseEntity<DataResponse<?>> verifyResetPasswordEmail(
            @Parameter(
                    description = "비밀번호를 저장하려는 회원에 대해서 발송된 링크 안에 포함된 UUID",
                    schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @RequestParam @NotNull(message = "식별자가 비어 있습니다.") UUID uuid,
            HttpServletResponse httpResponse
    ) {
        String resultAccessToken = controller.verifyResetPasswordEmail(uuid);

        setHttpOnlyCookie(resultAccessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "비밀번호 재설정 입력 검증 API", description = "인증성공 시 비밀번호 재설정 완료")
    @PostMapping("/auth/reset-password-request/verify/input")
    public ResponseEntity<DataResponse<?>> verifyResetPasswordInput(
            @RequestBody @Valid InputValidationRequest request,
            @Parameter(hidden = true)
            @NotNull(message = "인증 토큰이 비어 있습니다. ")
            @CookieValue(value = "Authorization", required = false) String accessToken,
            HttpServletResponse httpResponse
    ) {
        controller.verifyResetPasswordInput(request, accessToken);

        deleteHttpOnlyCookie(httpResponse);
        return ResponseEntity.ok(DataResponse.ok());
    }

    public void setHttpOnlyCookie(String accessToken, HttpServletResponse httpResponse) {
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        accessTokenCookie.setSecure(false);  // Secure 설정
        accessTokenCookie.setPath("/");    // 모든 경로에서 유효
        accessTokenCookie.setMaxAge(5 * 60); // 유효 기간: 5분 (Access 토큰의 유효 기간과 동일)
        accessTokenCookie.setAttribute("SameSite", "Lax");
        httpResponse.addCookie(accessTokenCookie);
    }

    public void deleteHttpOnlyCookie(HttpServletResponse httpResponse) {
        Cookie accessTokenCookie = new Cookie("Authorization", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setAttribute("SameSite", "Lax");
        httpResponse.addCookie(accessTokenCookie);
    }

}
