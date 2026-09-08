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
    @DisplayName("키스토어 설정으로 비대칭키 생성 활동 수행")
    void testInit_givenKeyStoreConfig_willCreateAsymmetricKeys() {
        // given & when & then
        assertNotNull(ReflectionTestUtils.getField(tokenProvider, "privateKey"));
        assertNotNull(ReflectionTestUtils.getField(tokenProvider, "publicKey"));
    }

    @Test
    @DisplayName("UUID와 claims로 문자열 반환")
    void testGenerateAccessToken_givenUuidAndClaims_willReturnString() {
        // given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();

        // when
        String accessToken = tokenProvider.generateAccessToken(uuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(uuid);

        // then
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertFalse(accessToken.isEmpty());
        assertFalse(refreshToken.isEmpty());
        assertEquals(3, accessToken.split("\\.").length);
        assertEquals(3, refreshToken.split("\\.").length);

        String payloadBase64 = accessToken.split("\\.")[1];
        String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64));
        assertTrue(payloadJson.contains("\"sub\":\"" + uuid + "\""));
        assertTrue(payloadJson.contains("\"nickname\":\"test\""));
        assertTrue(payloadJson.contains("\"roles\":\"ROLE_USER\""));
    }

    @Test
    @DisplayName("유효한 토큰으로 참 반환")
    void testValidateToken_givenValidToken_willReturnTrue() {
        // given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String accessToken = tokenProvider.generateAccessToken(uuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(uuid);

        // when
        boolean isAccessTokenValid = tokenProvider.validateToken(accessToken);
        boolean isRefreshTokenValid = tokenProvider.validateToken(refreshToken);

        // then
        assertTrue(isAccessTokenValid);
        assertTrue(isRefreshTokenValid);
    }

    @Test
    @DisplayName("변조된 payload 토큰으로 예외 반환")
    void testValidateToken_givenTamperedToken_willThrowException() {
        // given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String originalToken = tokenProvider.generateAccessToken(uuid, claims);

        String[] parts = originalToken.split("\\.");
        String header = parts[0];
        String payload = parts[1];
        String signature = parts[2];
        String decodePayload = new String(Base64.getUrlDecoder().decode(payload));
        String tamperedPayload = decodePayload.replace("\"nickname\":\"test\"", "\"nickname\":\"hacked\"");
        String encodedTamperedPayload = Base64.getUrlEncoder().encodeToString(tamperedPayload.getBytes());
        String tamperedToken = header + "." + encodedTamperedPayload + "." + signature;

        // when & then
        assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("유효하지 않은 서명 토큰으로 예외 반환")
    void testValidateToken_givenTamperedTokenWithInvalidSignature_willThrowException() {
        // given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String originalToken = tokenProvider.generateAccessToken(uuid, claims);

        String[] parts = originalToken.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + ".fake-signature";

        // when & then
        assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("토큰으로 Claims 반환")
    void testGetClaimsFromToken_givenToken_willReturnClaims() {
        // given
        UUID uuid = UUID.randomUUID();
        Map<String, String> claims = createDefaultClaims();
        String token = tokenProvider.generateAccessToken(uuid, claims);

        // when
        Claims extractedClaims = tokenProvider.getClaimsFromToken(token);

        // then
        assertThat(extractedClaims.getIssuer()).isEqualTo("https://app.modusplant.kr");
        assertThat(extractedClaims.getAudience()).contains("https://www.modusplant.kr");
        assertThat(extractedClaims.getSubject()).isEqualTo(String.valueOf(uuid));

        Date extractedIssuedAt = extractedClaims.getIssuedAt();
        Date extractedExpiration = extractedClaims.getExpiration();
        assertThat(extractedIssuedAt).isNotNull();
        assertThat(extractedExpiration).isNotNull();
        assertThat(extractedExpiration.getTime()).isEqualTo(extractedIssuedAt.getTime() + 1800000L);

        assertThat(extractedClaims.get("nickname", String.class)).isEqualTo(claims.get("nickname"));
        assertThat(extractedClaims.get("roles", String.class)).isEqualTo(claims.get("roles"));
    }

    @Test
    @DisplayName("토큰으로 UUID 반환")
    void testGetMemberUuidFromToken_givenToken_willReturnUuid() {
        // given
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // when
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(token);

        // then
        assertThat(memberUuid).isEqualTo(uuid);
    }

    @Test
    @DisplayName("토큰으로 Date 반환")
    void testGetIssuedAtFromToken_givenToken_willReturnDate() {
        // given
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // when
        Date issuedAt = tokenProvider.getIssuedAtFromToken(token);

        // then
        assertThat(issuedAt.getTime()).isCloseTo(now.getTime(), within(5000L));
    }

    @Test
    @DisplayName("토큰으로 Date 반환")
    void testGetExpirationFromToken_givenToken_willReturnDate() {
        // given
        UUID uuid = UUID.randomUUID();
        String token = tokenProvider.generateRefreshToken(uuid);

        // when
        Date issuedAt = tokenProvider.getIssuedAtFromToken(token);
        Date expiration = tokenProvider.getExpirationFromToken(token);

        // then
        long expectedExpiration = issuedAt.getTime() + 604800000L;
        assertThat(expiration.getTime()).isEqualTo(expectedExpiration);
    }

    private Map<String, String> createDefaultClaims() {
        return Map.of(
                "nickname", "test",
                "roles", "ROLE_USER"
        );
    }
}
