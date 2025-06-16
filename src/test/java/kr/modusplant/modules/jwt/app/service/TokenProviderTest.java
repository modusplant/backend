package kr.modusplant.modules.jwt.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import kr.modusplant.modules.jwt.error.TokenKeyCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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
    @DisplayName("비대칭키 생성 실패 테스트")
    void testInitFail() {
        try (MockedStatic<KeyPairGenerator> mockedStatic = Mockito.mockStatic(KeyPairGenerator.class)){
            mockedStatic.when(() -> KeyPairGenerator.getInstance("EC"))
                    .thenThrow(new NoSuchAlgorithmException("NoSuchAlgorithm"));

            TokenProvider tokenProvider = new TokenProvider();

            assertThatThrownBy(tokenProvider::init)
                    .isInstanceOf(TokenKeyCreationException.class)
                    .hasMessageContaining("Failed to create RefreshToken KeyPair");
        }
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

    @Test
    @DisplayName("토큰에서 정보 가져오기 테스트")
    void getClaimsFromTokenTest() {
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String token = tokenProvider.generateAccessToken(uuid,claims);

        // When
        Claims extractedClaims = tokenProvider.getClaimsFromToken(token);

        // Then
        assertThat(extractedClaims.getIssuer()).isEqualTo("test-issuer");
        assertThat(extractedClaims.getAudience()).contains("test-audience");
        assertThat(extractedClaims.getSubject()).isEqualTo(String.valueOf(uuid));

        Date extractedIssuedAt = extractedClaims.getIssuedAt();
        Date extractedExpiration = extractedClaims.getExpiration();
        assertThat(extractedIssuedAt).isNotNull();
        assertThat(extractedExpiration).isNotNull();
        assertThat(extractedExpiration.getTime()).isEqualTo(extractedIssuedAt.getTime()+900000L);

        assertThat(extractedClaims.get("nickname",String.class)).isEqualTo(claims.get("nickname"));
        assertThat(extractedClaims.get("role",String.class)).isEqualTo(claims.get("role"));
    }

    @Test
    @DisplayName("토큰에서 member UUID 추출")
    void getMemberUuidFromTokenTest() {
        // Given
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // When
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(token);

        // Then
        assertThat(memberUuid).isEqualTo(uuid);
    }

    @Test
    @DisplayName("토큰에서 issuedAt 추출")
    void getIssuedAtFromTokenTest() {
        // Given
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // When
        Date issuedAt = tokenProvider.getIssuedAtFromToken(token);

        // Then
        assertThat(issuedAt.getTime()).isCloseTo(now.getTime(),within(5000L));
    }

    @Test
    @DisplayName("토큰에서 expiration 추출")
    void getExpirationFromTokenTest() {
        // Given
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // When
        Date issuedAt = tokenProvider.getIssuedAtFromToken(token);
        Date expiration = tokenProvider.getExpirationFromToken(token);

        // Then
        long expectedExpiration = issuedAt.getTime() + 3600000L;
        assertThat(expiration.getTime()).isEqualTo(expectedExpiration);
    }

    private Map<String,String> createDefaultClaims() {
        return Map.of(
                "nickname", "test",
                "role","ROLE_USER"
        );
    }
}