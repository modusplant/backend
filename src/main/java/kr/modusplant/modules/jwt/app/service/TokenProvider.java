package kr.modusplant.modules.jwt.app.service;

ㅍimport io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kr.modusplant.modules.auth.email.app.http.request.VerifyEmailRequest;
import kr.modusplant.modules.jwt.error.InvalidTokenException;
import kr.modusplant.modules.jwt.error.TokenKeyCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.VERIFY_CODE;
import static kr.modusplant.global.vo.FieldName.EMAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {
    @Value("${jwt.iss}")
    private String iss;

    @Value("${jwt.aud}")
    private String aud;

    @Value("${jwt.access_duration}")
    private long accessDuration;

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    // 메일 API 비밀키 설정
    @Value("${mail-api.jwt-secret-key}")
    private String MAIL_API_JWT_SECRET_KEY;

    @PostConstruct
    public void init() {
        try {
            // ECDSA 키 생성
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(256);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new TokenKeyCreationException("Failed to create RefreshToken KeyPair", e);
        }
    }

    // Access RefreshToken 생성
    public String generateAccessToken(UUID uuid, Map<String,String> privateClaims) {
        Date now = new Date();
        Date iat = new Date(now.getTime());
        Date exp = new Date(iat.getTime() + accessDuration);

        return Jwts.builder()
                .issuer(iss)
                .subject(String.valueOf(uuid))
                .audience().add(aud).and()
                .issuedAt(iat)
                .expiration(exp)
                .claims(privateClaims)
                .signWith(privateKey)
                .compact();
    }

    // Refresh RefreshToken 생성
    public String generateRefreshToken(UUID uuid) {
        Date now = new Date();
        Date iat = new Date(now.getTime());
        Date exp = new Date(iat.getTime() + refreshDuration);

        return Jwts.builder()
                .issuer(iss)
                .subject(String.valueOf(uuid))
                .audience().add(aud).and()
                .issuedAt(iat)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }

    // 토큰 검증하기
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch(ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            return false;
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT 토큰입니다 : {}", e.getMessage());
            throw new InvalidTokenException("Invalid JWT RefreshToken");
        }
    }

    // 토큰에서 정보 가져오기
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch(ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다");
            throw new InvalidTokenException("Expired JWT RefreshToken");
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT 토큰입니다 : {}", e.getMessage());
            throw new InvalidTokenException("Invalid JWT RefreshToken");
        }
    }

    public UUID getMemberUuidFromToken(String token) {
        return UUID.fromString(getClaimsFromToken(token).getSubject());
    }

    public Date getIssuedAtFromToken(String token) {
        return getClaimsFromToken(token).getIssuedAt();
    }

    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

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
    public void validateVerifyAccessToken(String jwtToken, VerifyEmailRequest verifyEmailRequest) {
        String verifyCode = verifyEmailRequest.getVerifyCode();
        String email = verifyEmailRequest.getEmail();

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
                throw new RuntimeException("Invalid verification code");
            }
            if (!email.equals(claims.get(EMAIL, String.class))) {
                throw new RuntimeException("Invalid email address");
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
}
