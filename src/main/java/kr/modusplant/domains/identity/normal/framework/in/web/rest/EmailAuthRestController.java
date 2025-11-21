package kr.modusplant.domains.identity.normal.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.identity.normal.adapter.controller.EmailAuthController;
import kr.modusplant.domains.identity.normal.usecase.request.EmailAuthRequest;
import kr.modusplant.domains.identity.normal.usecase.request.EmailValidationRequest;
import kr.modusplant.domains.identity.normal.usecase.request.InputValidationRequest;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.REGEX_UUID;

@Tag(name = "이메일 API", description = "이메일 인증 메일 발송과 검증을 다루는 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class EmailAuthRestController {

    private final EmailAuthController controller;

    @Operation(summary = "인증 코드 메일 전송 API", description = "인증 코드를 포함하는 메일을 발송합니다.")
    @PostMapping("/members/verify-email/send")
    public ResponseEntity<DataResponse<?>> sendAuthEmail(
            @RequestBody @Valid EmailAuthRequest request,
            HttpServletResponse httpResponse
    ) {
        String accessToken = controller.sendAuthEmail(request);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "인증 코드 메일 검증 API", description = "인증을 위해 인증 코드를 검증합니다.")
    @PostMapping("/members/verify-email")
    public ResponseEntity<DataResponse<?>> verifyAuthEmailCode(
            @RequestBody @Valid EmailValidationRequest request,
            @CookieValue(value = "Authorization", required = false) String accessToken
    ) {
        controller.verifyAuthEmailCode(request, accessToken);

        return ResponseEntity.ok(
                DataResponse.ok(new HashMap<>() {{
                    put("hasEmailAuth", true);
                }}));
    }

    @Operation(summary = "비밀번호 재설정 메일 전송 API", description = "비밀번호 재설정 전용 하이퍼링크를 포함하는 메일을 발송합니다.")
    @PostMapping("/auth/reset-password-request/send")
    public ResponseEntity<DataResponse<?>> sendResetPasswordCode(
            @RequestBody @Valid EmailAuthRequest request, HttpServletResponse httpResponse
    ) {
        String accessToken = controller.sendResetPasswordCode(request);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "비밀번호 재설정 메일 검증 API", description = "인증을 위해 메일로부터 검증합니다.")
    @PostMapping("/auth/reset-password-request/verify/email")
    public ResponseEntity<DataResponse<?>> verifyEmailForResetPassword(
            @Parameter(
                    description = "비밀번호를 저장하려는 회원에 대해서 저장된 ID",
                    schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @NotNull(message = "식별자가 비어 있습니다. ") UUID uuid,
            @CookieValue(value = "Authorization", required = false) String accessToken,
            HttpServletResponse httpResponse
    ) {
        String resultAccessToken = controller.verifyEmailForResetPassword(uuid, accessToken);

        setHttpOnlyCookie(resultAccessToken, httpResponse);
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(
                        URI.create(
                                String.format("https://www.modusplant.kr/reset-password?uuid=%s", uuid))
                )
                .build();
    }

    @Operation(summary = "비밀번호 재설정 입력 검증 API", description = "인증을 위해 입력으로부터 검증합니다.")
    @PostMapping("/auth/reset-password-request/verify/input")
    public ResponseEntity<DataResponse<?>> verifyInputForResetPassword(
            @RequestBody @Valid InputValidationRequest request,
            @CookieValue(value = "Authorization", required = false) String accessToken,
            HttpServletResponse httpResponse
    ) {
        controller.verifyInputForResetPassword(request, accessToken);
        return ResponseEntity.ok(DataResponse.ok());
    }

    public void setHttpOnlyCookie(String accessToken, HttpServletResponse httpResponse) {
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        accessTokenCookie.setSecure(false);  // Secure 설정
        accessTokenCookie.setPath("/");    // 모든 경로에서 유효
        accessTokenCookie.setMaxAge(3 * 60); // 유효 기간: 3분 (Access 토큰의 유효 기간과 동일)
        accessTokenCookie.setAttribute("SameSite", "Lax");
        httpResponse.addCookie(accessTokenCookie);
    }

}
