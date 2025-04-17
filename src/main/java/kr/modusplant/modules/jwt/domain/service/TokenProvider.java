package kr.modusplant.modules.jwt.domain.service;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import kr.modusplant.modules.jwt.error.InvalidTokenException;
import kr.modusplant.modules.jwt.error.TokenKeyCreationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
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
            throw new TokenKeyCreationException("Failed to create RefreshToken KeyPair",e);
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
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch(ExpiredJwtException e) {
            logger.warn("만료된 JWT 토큰입니다.");
            return false;
        } catch (JwtException e) {
            logger.error("유효하지 않은 JWT 토큰입니다 : {}", e.getMessage());
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
            logger.warn("만료된 JWT 토큰입니다");
            throw new InvalidTokenException("Expired JWT RefreshToken");
        } catch (JwtException e) {
            logger.error("유효하지 않은 JWT 토큰입니다 : {}", e.getMessage());
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

}
