package kr.modusplant.modules.auth.email.app.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.auth.email.app.http.request.EmailRequest;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.auth.email.app.service.MailService;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static kr.modusplant.global.vo.CamelCaseWord.VERIFY_CODE;
import static kr.modusplant.global.vo.FieldName.EMAIL;

@Tag(name = "이메일 인증 API", description = "이메일 인증 메일 발송과 검증을 다루는 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmailAuthController {
    private final TokenProvider tokenProvider;
    private final MailService mailService;


    @Operation(summary = "본인 인증 메일 전송 API", description = "본인 인증을 위해 메일을 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Failed email handling due to client error")
    })
    @PostMapping("/members/verify-email/send")
    public ResponseEntity<DataResponse<?>> verify(
            @RequestBody @Valid EmailRequest request,
            HttpServletResponse httpResponse
    ) {
        String email = request.getEmail();
        // 인증코드 생성
        String verifyCode = tokenProvider.generateVerifyCode();
        // JWT 토큰 생성
        String accessToken = tokenProvider.generateVerifyAccessToken(email, verifyCode);

        mailService.callSendVerifyEmail(email, verifyCode);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(DataResponse.of(200, "OK: Succeeded"));
    }

    @Operation(summary = "본인 인증 메일 검증 API", description = "본인 인증을 위해 코드를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Failed verification due to client error")
    })
    @PostMapping("/members/verify-email")
    public ResponseEntity<DataResponse<?>> verifyEmail(
            @RequestBody VerifyEmailRequest verifyEmailRequest,
            @CookieValue(value = "Authorization", required = false) String accessToken
    ) {
        tokenProvider.validateVerifyAccessToken(accessToken, verifyEmailRequest.getVerifyCode());

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
