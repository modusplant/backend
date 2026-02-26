package kr.modusplant.infrastructure.jwt.provider;

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
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

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
    @DisplayName("비대칭키 생성 실패 테스트")
    void testInit_willThrowTokenKeyCreationException() {
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
}