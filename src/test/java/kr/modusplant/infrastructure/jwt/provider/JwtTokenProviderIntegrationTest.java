package kr.modusplant.infrastructure.jwt.provider;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderIntegrationTest {
    private final JwtTokenProvider tokenProvider;

    @Autowired
    JwtTokenProviderIntegrationTest(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Test
    @DisplayName("비대칭키 생성 여부 확인")
    void testInit_willCreateAsymmetricKeys() {
        assertNotNull(ReflectionTestUtils.getField(tokenProvider,"privateKey"));
        assertNotNull(ReflectionTestUtils.getField(tokenProvider,"publicKey"));
    }

    @Test
    @DisplayName("토큰 생성 여부 테스트")
    void testGenerateToken_givenUuid_willReturnValidJwt(){
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();

        // When
        String accessToken = tokenProvider.generateAccessToken(uuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(uuid);

        // Then
        // 토큰 값 형식 검증
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertFalse(accessToken.isEmpty());
        assertFalse(refreshToken.isEmpty());
        assertEquals(3, accessToken.split("\\.").length);
        assertEquals(3, refreshToken.split("\\.").length);

        // 토큰 페이로드 검증
        String payloadBase64 = accessToken.split("\\.")[1];
        String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64));
        assertTrue(payloadJson.contains("\"sub\":\"" + uuid + "\""));
        assertTrue(payloadJson.contains("\"nickname\":\"test\""));
        assertTrue(payloadJson.contains("\"role\":\"ROLE_USER\""));
    }

    @Test
    @DisplayName("유효한 토큰 검증 테스트")
    void testValidateToken_givenValidToken_willReturnTrue(){
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String accessToken = tokenProvider.generateAccessToken(uuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(uuid);

        // When
        boolean isAccessTokenValid = tokenProvider.validateToken(accessToken);
        boolean isRefreshTokenValid = tokenProvider.validateToken(refreshToken);

        // Then
        assertTrue(isAccessTokenValid);
        assertTrue(isRefreshTokenValid);
    }

    @Test
    @DisplayName("payload가 변조된 토큰 검증 테스트")
    void testValidateToken_givenTamperedToken_willThrowJwtException(){
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
    void testValidateToken_givenTamperedTokenWithInvalidSignature_willThrowJwtException() {
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
    void testGetClaimsFromToken_givenToken_willReturnInfo() {
        // Given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String token = tokenProvider.generateAccessToken(uuid, claims);

        // When
        Claims extractedClaims = tokenProvider.getClaimsFromToken(token);

        // Then
        assertThat(extractedClaims.getIssuer()).isEqualTo("https://app.modusplant.kr");
        assertThat(extractedClaims.getAudience()).contains("https://www.modusplant.kr");
        assertThat(extractedClaims.getSubject()).isEqualTo(String.valueOf(uuid));

        Date extractedIssuedAt = extractedClaims.getIssuedAt();
        Date extractedExpiration = extractedClaims.getExpiration();
        assertThat(extractedIssuedAt).isNotNull();
        assertThat(extractedExpiration).isNotNull();
        assertThat(extractedExpiration.getTime()).isEqualTo(extractedIssuedAt.getTime() + 1800000L);

        assertThat(extractedClaims.get("nickname",String.class)).isEqualTo(claims.get("nickname"));
        assertThat(extractedClaims.get("role",String.class)).isEqualTo(claims.get("role"));
    }

    @Test
    @DisplayName("토큰에서 member UUID 추출")
    void testGetMemberUuidFromToken_givenToken_willReturnMemberUuid() {
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
    void testGetIssuedAtFromToken_givenToken_willReturnIssuedAt() {
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
    void testGetExpirationFromToken_givenToken_willReturnExpiration() {
        // Given
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // When
        Date issuedAt = tokenProvider.getIssuedAtFromToken(token);
        Date expiration = tokenProvider.getExpirationFromToken(token);

        // Then
        long expectedExpiration = issuedAt.getTime() + 604800000L;
        assertThat(expiration.getTime()).isEqualTo(expectedExpiration);
    }

    private Map<String,String> createDefaultClaims() {
        return Map.of(
                "nickname", "test",
                "role","ROLE_USER"
        );
    }
}