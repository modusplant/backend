package kr.modusplant.infrastructure.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenKeyCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * 순수 JWT 제어 Provider
 *
 * 기능 : 순수 JWT 암호화 토큰 발급/검증/추출만 담당 (비즈니스 로직 없음)
 * 사용법 : TokenService로 해결되지 않는 세밀한 토큰 제어가 필요할 때 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
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
            throw new TokenKeyCreationException();
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
            return false;
        } catch (JwtException e) {
            throw new InvalidTokenException();
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
        } catch (JwtException e) {
            throw new InvalidTokenException();
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

    // TODO: EMAIL 인증 관련 JWT 추가 필요
}
