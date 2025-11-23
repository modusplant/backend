package kr.modusplant.domains.identity.normal.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.modusplant.domains.identity.normal.usecase.request.EmailValidationRequest;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenExpiredException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAuthTokenHelper {

    // 메일 API 비밀키 설정
    @Value("${mail-api.jwt-secret-key}")
    private String MAIL_API_JWT_SECRET_KEY;

    private final String SCOPE = "scope";
    private final String VERIFY_CODE = "verifyCode";

    /**
     *  이메일 인증 관련 JWT 토큰 메소드
     */
    public String generateEmailAuthVerifyAccessToken(String email, String verifyCode) {
        // 만료 시간 설정 (3분 뒤)
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 3 * 60 * 1000);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims()
                .issuedAt(now)
                .expiration(expirationDate)
                .add(SCOPE, new String[]{"EmailAuthVerify"})
                .add("email", email)
                .add(VERIFY_CODE, verifyCode)
                .and()
                .signWith(Keys.hmacShaKeyFor(MAIL_API_JWT_SECRET_KEY.getBytes()))
                .compact();
    }

    // TODO : Spring Security 적용 후 필터에서 쿠키 검증 로직 추가된 후 테스트 필요
    public void validateEmailAuthVerifyAccessToken(EmailValidationRequest verifyEmailRequest, String jwtToken) {
        String verifyCode = verifyEmailRequest.verifyCode();
        String email = verifyEmailRequest.email();

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            Claims claims = getClaims(jwtToken);

            // JWT 범위 검증
            String[] payloadScope = claims.get(SCOPE, String[].class);
            if (!List.of(payloadScope).contains("EmailAuthVerify")) {
                throw new InvalidTokenException();
            }

            // JWT 인증코드 검증
            String payloadVerifyCode = claims.get(VERIFY_CODE, String.class);

            // 인증코드, 이메일 일치 검증
            if (!verifyCode.equals(payloadVerifyCode)) {
                throw new InvalidDataException(ErrorCode.INVALID_EMAIL_VERIFY_CODE, "verifyCode");
            }
            if (!email.equals(claims.get("email", String.class))) {
                throw new InvalidDataException(ErrorCode.INVALID_EMAIL, "email");
            }
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String generateResetPasswordAccessToken(String email, String scope) {
        // 만료 시간 설정 (3분 뒤)
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 3 * 60 * 1000);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims()
                .issuedAt(now)
                .expiration(expirationDate)
                .add(SCOPE, new String[]{scope})
                .add("email", email)
                .and()
                .signWith(Keys.hmacShaKeyFor(MAIL_API_JWT_SECRET_KEY.getBytes()))
                .compact();
    }

    // TODO : Spring Security 적용 후 필터에서 쿠키 검증 로직 추가된 후 테스트 필요
    public void validateResetPasswordAccessToken(String email, String jwtToken) {
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            Claims claims = getClaims(jwtToken);

            // JWT 범위 검증
            String[] payloadScope = claims.get(SCOPE, String[].class);
            if (!List.of(payloadScope).contains("resetPasswordEmail")) {
                throw new InvalidTokenException();
            }

            // 이메일 일치 검증
            if (!email.equals(claims.get("email", String.class))) {
                throw new InvalidDataException(ErrorCode.INVALID_EMAIL, "email");
            }
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // TODO : Spring Security 적용 후 필터에서 쿠키 검증 로직 추가된 후 테스트 필요
    public void validateResetPasswordAccessTokenForInput(String jwtToken) {
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            Claims claims = getClaims(jwtToken);

            // JWT 범위 검증
            String[] payloadScope = claims.get(SCOPE, String[].class);
            if (!List.of(payloadScope).contains("resetPasswordInput")) {
                throw new InvalidTokenException();
            }
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Claims getClaims(String jwtToken) {
        // JWT 토큰 파싱
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(MAIL_API_JWT_SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
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
}
