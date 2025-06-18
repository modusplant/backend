package kr.modusplant.modules.auth.email.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.auth.email.app.http.request.EmailRequest;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.auth.email.app.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Tag(name = "이메일 인증 API", description = "이메일 인증 메일 발송과 검증을 다루는 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @Operation(summary = "본인 인증 메일 전송 API", description = "본인 인증을 위해 메일을 전송합니다.")
    @PostMapping("/members/verify-email/send")
    public ResponseEntity<DataResponse<?>> verify(
            @RequestBody @Valid EmailRequest request,
            HttpServletResponse httpResponse
    ) {
        String accessToken = emailAuthService.sendVerifyEmail(request);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "본인 인증 메일 검증 API", description = "본인 인증을 위해 코드를 검증합니다.")
    @PostMapping("/members/verify-email")
    public ResponseEntity<DataResponse<?>> verifyEmail(
            @RequestBody @Valid VerifyEmailRequest verifyEmailRequest,
            @CookieValue(value = "Authorization", required = false) String accessToken
    ) {
        emailAuthService.verifyEmail(verifyEmailRequest, accessToken);

        return ResponseEntity.ok(
                DataResponse.ok(new HashMap<>() {{
                    put("hasEmailAuth", true);
                }}));
    }

    @Operation(summary = "비밀번호 재설정 요청 API", description = "비밀번호 재설정 시 본인인증 코드를 메일로 발송합니다.")
    @PostMapping("/auth/reset-password-request/send")
    public ResponseEntity<DataResponse<?>> sendResetPasswordCode(
            @RequestBody @Valid EmailRequest request
    ) {
        emailAuthService.sendResetPasswordCode(request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "비밀번호 재설정 검증 API", description = "비밀번호 재설정 본인인증 코드를 검증합니다.")
    @PostMapping("/auth/reset-password-request/verify")
    public ResponseEntity<DataResponse<?>> verifyResetPasswordCode(
            @RequestBody @Valid VerifyEmailRequest request
    ) {
        emailAuthService.verifyResetPasswordCode(request);
        return ResponseEntity.ok(
                DataResponse.ok(new HashMap<>() {{
                    put("hasEmailAuth", true);
                }}));
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
}
