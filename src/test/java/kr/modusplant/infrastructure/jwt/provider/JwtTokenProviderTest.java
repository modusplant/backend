package kr.modusplant.infrastructure.jwt.provider;

import kr.modusplant.infrastructure.jwt.exception.TokenKeyCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        ReflectionTestUtils.setField(tokenProvider,"keyStorePath","testKeystore.p12");
        ReflectionTestUtils.setField(tokenProvider,"keyStorePassword","testKeystorePassword");
        ReflectionTestUtils.setField(tokenProvider,"keyStoreType","PKCS12");
        ReflectionTestUtils.setField(tokenProvider,"keyAlias","test-modusplant-server");
    }

//    @Test
//    @DisplayName("비대칭키 생성 실패 테스트")
//    void testInit_willThrowTokenKeyCreationException() {
//        try (MockedStatic<KeyPairGenerator> mockedStatic = Mockito.mockStatic(KeyPairGenerator.class)){
//            mockedStatic.when(() -> KeyPairGenerator.getInstance("EC"))
//                    .thenThrow(new NoSuchAlgorithmException("NoSuchAlgorithm"));
//
//            assertThatThrownBy(tokenProvider::init)
//                    .isInstanceOf(TokenKeyCreationException.class)
//                    .hasMessage("서버의 문제로 인증을 처리하지 못했습니다");
//        }
//    }
}