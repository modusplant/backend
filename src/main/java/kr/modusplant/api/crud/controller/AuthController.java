package kr.modusplant.api.crud.controller;

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
import kr.modusplant.api.crud.model.request.EmailRequest;
import kr.modusplant.api.crud.model.request.VerifyEmailRequest;
import kr.modusplant.api.crud.model.response.SingleDataResponse;
import kr.modusplant.api.crud.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final MailService mailService;

    // 비밀키 설정
    private static final String SECRET_KEY = "modusplant0fwskslsoi021jscsmakdnkwlqdjskdjdksaldndwoqpwo";

    @Operation(
            summary = "본인인증 메일 전송 API",
            description = "회원가입 시 본인인증 메일 전송을 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/members/verify-email/send")
    public ResponseEntity<SingleDataResponse> verify(
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

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", 200);
        metadata.put("message", "OK: Succeeded");

        SingleDataResponse response = new SingleDataResponse<>();
        response.setMetadata(metadata);

        // JWT AccessToken 설정
        setHttpOnlyCookie(accessToken, httpResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "본인인증 메일 검증 API", description = "회원가입 시 본인인증 코드를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/members/verify-email")
    public ResponseEntity<SingleDataResponse> verifyEmail(
            @RequestBody VerifyEmailRequest verifyEmailRequest,
            @CookieValue(value = "Authorization", required = false) String accessToken
    ) {

        // JwtToken 에 담긴 데이터 조회 테스트용
        validateVerifyAccessToken(accessToken, verifyEmailRequest.getVerifyCode());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", HttpStatus.OK.value());
        metadata.put("message", "OK: Succeeded");

        Map<String, Object> data = new HashMap<>();
        data.put("hasEmailAuth", true);

        SingleDataResponse response = new SingleDataResponse<>();
        response.setMetadata(metadata);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    // TODO : JWT Util 구현완료 시 옮기는것을 예상하고 작업 중
    public String generateVerifyAccessToken(String email, String verifyCode) {
        // 만료 시간 설정
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 5 * 60 * 1000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("email", email)
                .claim("verifyCode", verifyCode)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public void validateVerifyAccessToken(String jwtToken, String verifyCode) {
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            // JWT 토큰 파싱
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            // JWT 토큰 검증
            String payloadEmail = claims.get("email", String.class);
            String payloadverifyCode = claims.get("verifyCode", String.class);

            // 발급된 인증코드와 메일 인증코드 일치 검증
            if (!verifyCode.equals(payloadverifyCode)) {
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
        accessTokenCookie.setAttribute("SameSite", "Strict");
        httpResponse.addCookie(accessTokenCookie);
    }
}
