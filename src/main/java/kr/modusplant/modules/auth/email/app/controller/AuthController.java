package kr.modusplant.modules.auth.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.auth.email.app.http.request.EmailRequest;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.auth.email.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "본인인증 메일 전송 API",
            description = "회원가입 시 본인인증 메일 전송을 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/members/verify-email/send")
    public ResponseEntity<DataResponse<?>> verify(
            @RequestBody @Valid EmailRequest request,
            HttpServletResponse httpResponse
    ) {
        String accessToken = authService.sendVerifyEmail(request);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.of(200, "OK: Succeeded"));
    }

    @Operation(summary = "본인인증 메일 검증 API", description = "회원가입 시 본인인증 코드를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/members/verify-email")
    public ResponseEntity<DataResponse<?>> verifyEmail(
            @RequestBody VerifyEmailRequest verifyEmailRequest,
            @CookieValue(value = "Authorization", required = false) String accessToken
    ) {
        authService.verifyEmail(verifyEmailRequest, accessToken);

        return ResponseEntity.ok(
                DataResponse.ok(new HashMap<>() {{
                    put("hasEmailAuth", true);
                }}));
    }

    @Operation(summary = "비밀번호 재설정 요청 API", description = "비밀번호 재설정 시 본인인증 코드를 메일로 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/auth/reset-password-request/send")
    public ResponseEntity<DataResponse<?>> sendResetPasswordCode(
            @RequestBody @Valid EmailRequest request
    ) {
        authService.sendResetPasswordCode(request);
        return ResponseEntity.ok().body(DataResponse.of(200, "OK: Succeeded"));
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
