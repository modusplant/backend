package kr.modusplant.domains.normalidentity.normal.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.modusplant.domains.normalidentity.normal.usecase.request.EmailValidationRequest;
import kr.modusplant.infrastructure.jwt.exception.TokenExpiredException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.EMAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAuthTokenHelper {

    // 메일 API 비밀키 설정
    @Value("${mail-api.jwt-secret-key}")
    private String MAIL_API_JWT_SECRET_KEY;

    private final String VERIFY_CODE = "verifyCode";

    /**
     *  이메일 인증 관련 JWT 토큰 메소드
     */
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
                .signWith(Keys.hmacShaKeyFor(MAIL_API_JWT_SECRET_KEY.getBytes()))
                .compact();
    }

    // TODO : Spring Security 적용 후 필터에서 쿠키 검증 로직 추가된 후 테스트 필요
    public void validateVerifyAccessToken(EmailValidationRequest verifyEmailRequest, String jwtToken) {
        String verifyCode = verifyEmailRequest.verifyCode();
        String email = verifyEmailRequest.email();

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            // JWT 토큰 파싱
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(MAIL_API_JWT_SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

            // JWT 토큰 검증
            String payloadVerifyCode = claims.get(VERIFY_CODE, String.class);

            // 인증코드, 메일 일치 검증
            if (!verifyCode.equals(payloadVerifyCode)) {
                throw new InvalidDataException(ErrorCode.INVALID_EMAIL_VERIFY_CODE, "verifyCode");
            }
            if (!email.equals(claims.get(EMAIL, String.class))) {
                throw new InvalidDataException(ErrorCode.INVALID_EMAIL, "email");
            }
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
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
}
