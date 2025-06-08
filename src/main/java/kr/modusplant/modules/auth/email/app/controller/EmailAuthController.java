package kr.modusplant.modules.auth.email.app.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.auth.email.app.http.request.EmailRequest;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.auth.email.app.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static kr.modusplant.global.vo.CamelCaseWord.VERIFY_CODE;
import static kr.modusplant.global.vo.FieldName.EMAIL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmailAuthController {
    private final MailService mailService;

    // 비밀키 설정
    @Value("${mail-api.jwt-secret-key}")
    private String JWT_SECRET_KEY;

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
        String email = request.getEmail();
        // 인증코드 생성
        String verifyCode = generateVerifyCode();
        // JWT 토큰 생성
        String accessToken = generateVerifyAccessToken(email, verifyCode);

        // TODO : 메일 발송
        mailService.callSendVerifyEmail(email, verifyCode);

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

        // JwtToken 에 담긴 데이터 조회 테스트용
        validateVerifyAccessToken(accessToken, verifyEmailRequest.getVerifyCode());

        return ResponseEntity.ok(
                DataResponse.ok(new HashMap<>() {{
                    put("hasEmailAuth", true);
                }}));
    }

    // TODO : JWT Util or Provider 구현 완료 시 옮기는 것을 예상하고 작업 중
    public String generateVerifyAccessToken(String email, String verifyCode) {
        // 만료 시간 설정 (5분 뒤)
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 5 * 60 * 1000);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims()
                .issuedAt(now)
                .expiration(expirationDate)
                .add(EMAIL, email)
                .add(VERIFY_CODE, verifyCode)
                .and()
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()))
                .compact();
    }

    // TODO : Spring Security 적용 후 필터에서 쿠키 검증 로직 추가된 후 테스트 필요
    public void validateVerifyAccessToken(String jwtToken, String verifyCode) {
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            // JWT 토큰 파싱
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

            // JWT 토큰 검증
            String payloadVerifyCode = claims.get(VERIFY_CODE, String.class);

            // 발급된 인증코드와 메일 인증코드 일치 검증
            if (!verifyCode.equals(payloadVerifyCode)) {
                throw new RuntimeException("Invalid verification code");
            }
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Expired JWT token");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String generateVerifyCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
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
