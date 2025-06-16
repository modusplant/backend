package kr.modusplant.modules.jwt.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.mapper.RefreshTokenAppInfraMapper;
import kr.modusplant.modules.jwt.mapper.RefreshTokenAppInfraMapperImpl;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
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
class TokenValidationServiceTest implements RefreshTokenTestUtils, RefreshTokenEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {
    @Mock
    private RefreshTokenRepository tokenRepository;
    @Mock
    private SiteMemberRepository memberRepository;
    @InjectMocks
    private TokenValidationService tokenValidationService;
    @Spy
    private final RefreshTokenAppInfraMapper tokenMapper = new RefreshTokenAppInfraMapperImpl();

    @Nested
    class validateNotFoundMemberUuidTest {
        @Test
        @DisplayName("memberUuid가 없으면 예외 발생")
        void throwIfMemberUuidNotFound() {
            UUID memberUuid = UUID.randomUUID();
            given(memberRepository.existsByUuid(memberUuid)).willReturn(false);

            assertThatThrownBy(() -> tokenValidationService.validateNotFoundMemberUuid(memberUuid))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("memberUuid가 null이면 예외 발생")
        void throwIfMemberUuidIsNull() {
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundMemberUuid(null))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("memberUuid가 존재하면 예외 없음")
        void passIfMemberExists() {
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            given(memberRepository.existsByUuid(memberEntity.getUuid())).willReturn(true);

            assertThatCode(() -> tokenValidationService.validateNotFoundMemberUuid(memberEntity.getUuid()))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateNotFoundTokenUuidTest {
        @Test
        @DisplayName("tokenUuid가 없으면 예외 발생")
        void throwIfTokenNotFound() {
            UUID uuid = UUID.randomUUID();
            given(tokenRepository.existsByUuid(uuid)).willReturn(false);

            assertThatThrownBy(() -> tokenValidationService.validateNotFoundTokenUuid(uuid))
                    .isInstanceOf(EntityNotFoundWithUuidException.class);
        }

        @Test
        @DisplayName("tokenUuid가 null이면 예외 발생")
        void throwIfTokenUuidIsNull() {
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundTokenUuid(null))
                    .isInstanceOf(EntityNotFoundWithUuidException.class);
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
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("refresh token이 null이면 예외 발생")
        void throwIfRefreshTokenIsNull() {
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundRefreshToken(null))
                    .isInstanceOf(EntityNotFoundException.class);
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