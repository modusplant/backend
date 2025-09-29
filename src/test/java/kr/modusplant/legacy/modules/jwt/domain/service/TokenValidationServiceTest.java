package kr.modusplant.legacy.modules.jwt.domain.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.legacy.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.legacy.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.legacy.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.legacy.modules.jwt.error.TokenNotFoundException;
import kr.modusplant.legacy.modules.jwt.mapper.RefreshTokenAppInfraMapper;
import kr.modusplant.legacy.modules.jwt.mapper.RefreshTokenAppInfraMapperImpl;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.legacy.modules.jwt.persistence.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TokenValidationServiceTest implements RefreshTokenTestUtils, RefreshTokenEntityTestUtils, SiteMemberEntityTestUtils {
    @Mock
    private RefreshTokenRepository tokenRepository;
    @Mock
    private SiteMemberRepository memberRepository;
    @InjectMocks
    private TokenValidationService tokenValidationService;
    @Spy
    private final RefreshTokenAppInfraMapper tokenMapper = new RefreshTokenAppInfraMapperImpl();

    @Nested
    class validateNotFoundTokenUuidTest {
        @Test
        @DisplayName("tokenUuid가 없으면 예외 발생")
        void throwIfTokenNotFound() {
            UUID uuid = UUID.randomUUID();
            given(tokenRepository.existsByUuid(uuid)).willReturn(false);

            assertThatThrownBy(() -> tokenValidationService.validateNotFoundTokenUuid(uuid))
                    .isInstanceOf(TokenNotFoundException.class);
        }

        @Test
        @DisplayName("tokenUuid가 null이면 예외 발생")
        void throwIfTokenUuidIsNull() {
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundTokenUuid(null))
                    .isInstanceOf(TokenNotFoundException.class);
        }

        @Test
        @DisplayName("tokenUuid가 존재하면 예외 없음")
        void passIfTokenExists() {
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                    .uuid(UUID.randomUUID())
                    .member(memberEntity)
                    .build();
            RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);
            given(tokenRepository.existsByUuid(tokenEntity.getUuid())).willReturn(true);

            assertThatCode(() -> tokenValidationService.validateNotFoundTokenUuid(token.getUuid()))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateNotFoundRefreshTokenTest {
        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3s";

        @Test
        @DisplayName("refresh token이 없으면 예외 발생")
        void throwIfRefreshTokenNotFound() {
            given(tokenRepository.existsByRefreshToken(refreshToken)).willReturn(false);

            assertThatThrownBy(() -> tokenValidationService.validateNotFoundRefreshToken(refreshToken))
                    .isInstanceOf(TokenNotFoundException.class);
        }

        @Test
        @DisplayName("refresh token이 null이면 예외 발생")
        void throwIfRefreshTokenIsNull() {
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundRefreshToken(null))
                    .isInstanceOf(TokenNotFoundException.class);
        }

        @Test
        @DisplayName("refresh token이 있으면 예외 없음")
        void passIfRefreshTokenExists() {
            given(tokenRepository.existsByRefreshToken(refreshToken)).willReturn(true);

            assertThatCode(() -> tokenValidationService.validateNotFoundRefreshToken(refreshToken))
                    .doesNotThrowAnyException();
        }
    }

}