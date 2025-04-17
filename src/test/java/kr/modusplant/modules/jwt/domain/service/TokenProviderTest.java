package kr.modusplant.modules.jwt.domain.service;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        // @Value 애노테이션으로 주입되는 값을 설정
        ReflectionTestUtils.setField(tokenProvider,"iss","test-issuer");
        ReflectionTestUtils.setField(tokenProvider,"aud","test-audience");
        ReflectionTestUtils.setField(tokenProvider,"accessDuration",900000L);
        ReflectionTestUtils.setField(tokenProvider,"refreshDuration",3600000L);
        // init() 메서드 호출을 통해 공개키, 개인키 (ECDSA) 생성
        tokenProvider.init();
    }

    @Test
    @DisplayName("비대칭키 생성 테스트")
    void testInit() {
        assertNotNull(ReflectionTestUtils.getField(tokenProvider,"privateKey"));
        assertNotNull(ReflectionTestUtils.getField(tokenProvider,"publicKey"));
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    void generateTokenShouldReturnValidJwt(){
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();

        // When
        String accessToken = tokenProvider.generateAccessToken(uuid,claims);
        String refreshToken = tokenProvider.generateRefreshToken(uuid);

        // Then
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertFalse(accessToken.isEmpty());
        assertFalse(refreshToken.isEmpty());
        assertEquals(3, accessToken.split("\\.").length);
        assertEquals(3, refreshToken.split("\\.").length);
        // payload 검증
        String payloadBase64 = accessToken.split("\\.")[1];
        String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64));
        assertTrue(payloadJson.contains("\"sub\":\"" + uuid + "\""));
        assertTrue(payloadJson.contains("\"nickname\":\"test\""));
        assertTrue(payloadJson.contains("\"role\":\"ROLE_USER\""));
    }

    @Test
    @DisplayName("유효한 토큰 검증 테스트")
    void validateValidToken(){
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String accessToken = tokenProvider.generateAccessToken(uuid,claims);
        String refreshToken = tokenProvider.generateRefreshToken(uuid);

        // When
        boolean isAccessTokenValid = tokenProvider.validateToken(accessToken);
        boolean isRefreshTokenValid = tokenProvider.validateToken(refreshToken);

        // Then
        assertTrue(isAccessTokenValid);
        assertTrue(isRefreshTokenValid);
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void validateTokenShouldFailOnExpiredToken() throws ExpiredJwtException, InterruptedException {
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();

        ReflectionTestUtils.setField(tokenProvider, "accessDuration", 100L);
        String expiredToken = tokenProvider.generateAccessToken(uuid,claims);
        Thread.sleep(150);

        // When
        boolean isValid = tokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("payload가 변조된 토큰 검증 테스트")
    void validateTokenShouldFailOnTamperedTokenWithJwtException(){
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String originalToken = tokenProvider.generateAccessToken(uuid,claims);

        // When
        String[] parts = originalToken.split("\\.");
        String header = parts[0];
        String payload = parts[1];
        String signature = parts[2];
        // payload 변조 (Base64)
        String decodePayload = new String(Base64.getUrlDecoder().decode(payload));
        String tamperedPayload = decodePayload.replace("\"nickname\":\"test\"", "\"nickname\":\"hacked\"");
        String encodedTamperedPayload = Base64.getUrlEncoder().encodeToString(tamperedPayload.getBytes());
        String tamperedToken = header + "." + encodedTamperedPayload + "." + signature;

        // Then
        assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("유효하지 않은 Signature를 가진 토큰 검증")
    void validateTokenShouldFailOnInvalidSignature() {
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String originalToken = tokenProvider.generateAccessToken(uuid,claims);

        // When
        String[] parts = originalToken.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + ".fake-signature";

        // Then
        assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(tamperedToken));
    }


    private Map<String,String> createDefaultClaims() {
        return Map.of(
                "nickname", "test",
                "role","ROLE_USER"
        );
    }
}