package kr.modusplant.infrastructure.jwt.provider;

import kr.modusplant.infrastructure.jwt.claim.UploadTokenClaims;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenKeyCreationException;
import kr.modusplant.infrastructure.jwt.exception.enums.AuthTokenErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.record.common.constant.RecordConstant.TEST_RECORD_ORIGINAL_FILENAME;
import static kr.modusplant.domains.record.common.constant.RecordConstant.TEST_RECORD_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class JwtTokenProviderTest {
    @SuppressWarnings("FieldCanBeLocal")
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        // @Value 어노테이션으로 주입되는 값을 설정
        ReflectionTestUtils.setField(tokenProvider,"iss","test-issuer");
        ReflectionTestUtils.setField(tokenProvider,"aud","test-audience");
        ReflectionTestUtils.setField(tokenProvider,"accessDuration",900000L);
        ReflectionTestUtils.setField(tokenProvider,"refreshDuration",3600000L);
        ReflectionTestUtils.setField(tokenProvider,"keyStorePassword","testKeystorePassword");
        ReflectionTestUtils.setField(tokenProvider,"keyStoreType","PKCS12");
        ReflectionTestUtils.setField(tokenProvider,"keyAlias","test-modusplant-server");
        ReflectionTestUtils.setField(tokenProvider,"keyStoreFilename","test-modusplant-server-keystore.p12");
    }

    @Test
    @DisplayName("키 생성 실패 시 예외 반환")
    void testInit_givenKeyGenerationFailure_willThrowException() {
        try (
                MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class);
                MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
                MockedStatic<KeyPairGenerator> mockedKeyPairGeneration = Mockito.mockStatic(KeyPairGenerator.class)
        ) {
            Path mockedPathForUserHome = Mockito.mock(Path.class);
            Path mockedPathForKeyStoreFile = Mockito.mock(Path.class);
            given(mockedPathForUserHome.resolve("test-modusplant-server-keystore.p12")).willReturn(mockedPathForKeyStoreFile);
            mockedPaths.when(() -> Paths.get(System.getProperty("user.home")))
                    .thenReturn(mockedPathForUserHome);
            mockedFiles.when(() -> Files.exists(any()))
                    .thenReturn(false);
            mockedKeyPairGeneration.when(() -> KeyPairGenerator.getInstance("EC"))
                    .thenThrow(new NoSuchAlgorithmException("NoSuchAlgorithm"));

            assertThatThrownBy(tokenProvider::init)
                    .isInstanceOf(TokenKeyCreationException.class)
                    .extracting("errorCode")
                    .isEqualTo(AuthTokenErrorCode.INTERNAL_AUTHENTICATION_FAIL);
        }
    }

    private static final String TEST_VIDEO_KEY =
            "record/" + MEMBER_BASIC_USER_UUID + "/" + TEST_RECORD_ULID + "/video.mp4";

    private void initKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        KeyPair keyPair = keyGen.generateKeyPair();
        ReflectionTestUtils.setField(tokenProvider, "privateKey", keyPair.getPrivate());
        ReflectionTestUtils.setField(tokenProvider, "publicKey", keyPair.getPublic());
    }

    private Map<String, String> uploadClaims(String scope) {
        return Map.of(
                "scope", scope,
                "memberId", MEMBER_BASIC_USER_UUID.toString(),
                "videoKey", TEST_VIDEO_KEY,
                "originalFilename", TEST_RECORD_ORIGINAL_FILENAME);
    }

    @Test
    @DisplayName("유효한 업로드 토큰으로 UploadTokenClaims 반환")
    void testGetUploadTokenClaims_givenValidToken_willReturnUploadTokenClaims() throws Exception {
        // given
        initKeys();
        String token = tokenProvider.generateToken(TEST_RECORD_ULID, uploadClaims("uploadRecord"), 600_000L);

        // when
        UploadTokenClaims result = tokenProvider.getUploadTokenClaims(token);

        // then
        assertThat(result).isEqualTo(new UploadTokenClaims(
                TEST_RECORD_ULID, MEMBER_BASIC_USER_UUID, TEST_VIDEO_KEY, TEST_RECORD_ORIGINAL_FILENAME));
    }

    @Test
    @DisplayName("다른 scope의 토큰으로 예외 반환")
    void testGetUploadTokenClaims_givenWrongScope_willThrowException() throws Exception {
        // given
        initKeys();
        String token = tokenProvider.generateToken(TEST_RECORD_ULID, uploadClaims("resetPasswordEmail"), 600_000L);

        // when & then
        assertThatThrownBy(() -> tokenProvider.getUploadTokenClaims(token))
                .isInstanceOf(InvalidTokenException.class)
                .extracting("errorCode")
                .isEqualTo(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("scope가 없는 토큰으로 예외 반환")
    void testGetUploadTokenClaims_givenMissingScope_willThrowException() throws Exception {
        // given
        initKeys();
        String token = tokenProvider.generateToken(TEST_RECORD_ULID, Map.of("memberId", MEMBER_BASIC_USER_UUID.toString()), 600_000L);

        // when & then
        assertThatThrownBy(() -> tokenProvider.getUploadTokenClaims(token))
                .isInstanceOf(InvalidTokenException.class)
                .extracting("errorCode")
                .isEqualTo(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("위조된 토큰으로 예외 반환")
    void testGetUploadTokenClaims_givenMalformedToken_willThrowException() throws Exception {
        // given
        initKeys();
        String token = tokenProvider.generateToken(TEST_RECORD_ULID, uploadClaims("uploadRecord"), 600_000L);
        String tampered = token.substring(0, token.length() - 4) + (token.endsWith("AAAA") ? "BBBB" : "AAAA");

        // when & then
        assertThatThrownBy(() -> tokenProvider.getUploadTokenClaims("not-a-jwt"))
                .isInstanceOf(InvalidTokenException.class);
        assertThatThrownBy(() -> tokenProvider.getUploadTokenClaims(tampered))
                .isInstanceOf(InvalidTokenException.class)
                .extracting("errorCode")
                .isEqualTo(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰으로 예외 반환")
    void testGetUploadTokenClaims_givenForeignSignature_willThrowException() throws Exception {
        // given
        initKeys();
        String token = tokenProvider.generateToken(TEST_RECORD_ULID, uploadClaims("uploadRecord"), 600_000L);
        initKeys();

        // when & then
        assertThatThrownBy(() -> tokenProvider.getUploadTokenClaims(token))
                .isInstanceOf(InvalidTokenException.class)
                .extracting("errorCode")
                .isEqualTo(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("만료된 업로드 토큰으로 예외 반환")
    void testGetUploadTokenClaims_givenExpiredToken_willThrowException() throws Exception {
        // given
        initKeys();
        String token = tokenProvider.generateToken(TEST_RECORD_ULID, uploadClaims("uploadRecord"), -60_000L);

        // when & then
        assertThatThrownBy(() -> tokenProvider.getUploadTokenClaims(token))
                .isInstanceOf(InvalidTokenException.class)
                .extracting("errorCode")
                .isEqualTo(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
