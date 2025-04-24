package kr.modusplant.modules.jwt.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.domain.service.supers.RefreshTokenCrudService;
import kr.modusplant.modules.jwt.mapper.entity.RefreshTokenEntityMapper;
import kr.modusplant.modules.jwt.mapper.entity.RefreshTokenEntityMapperImpl;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TokenValidationServiceTest implements RefreshTokenTestUtils, RefreshTokenEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {
    @Mock
    private RefreshTokenCrudService refreshTokenCrudService;
    @Mock
    private RefreshTokenJpaRepository tokenRepository;
    @Mock
    private SiteMemberRepository memberRepository;
    @InjectMocks
    private TokenValidationService tokenValidationService;
    @Spy
    private final RefreshTokenEntityMapper tokenMapper = new RefreshTokenEntityMapperImpl();
    @Spy
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Nested
    class validateNotFoundRefreshTokenTest {
        @Test
        @DisplayName("Refresh Token이 없으면 true 반환")
        void returnTrueWhenRefreshTokenMissing() {
            // given
            String refreshToken = "refreshToken";
            given(refreshTokenCrudService.getByRefreshToken(refreshToken)).willReturn(Optional.empty());

            // when
            boolean result = tokenValidationService.validateNotFoundRefreshToken(refreshToken);

            // then
            assertTrue(result);
        }

        @Test
        @DisplayName("Refresh Token이 존재하면 false 반환")
        void returnFalseWhenRefreshTokenExists() {
            // given
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                    .uuid(UUID.randomUUID())
                    .member(memberEntity)
                    .build();
            RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);
            given(refreshTokenCrudService.getByRefreshToken(token.getRefreshToken())).willReturn(Optional.of(token));

            // when
            boolean result = tokenValidationService.validateNotFoundRefreshToken(token.getRefreshToken());

            // then
            assertFalse(result);
        }
    }

    @Nested
    class validateExistedDeviceIdTest {
        @Test
        @DisplayName("Device Id가 존재하면 true 반환")
        void returnTrueIfDeviceIdExists() {
            // given
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                    .uuid(UUID.randomUUID())
                    .member(memberEntity)
                    .build();
            RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);
            given(refreshTokenCrudService.getByDeviceId(token.getDeviceId())).willReturn(Optional.of(token));

            // when
            boolean result = tokenValidationService.validateExistedDeviceId(token.getDeviceId());

            // then
            assertTrue(result);
        }

        @Test
        @DisplayName("Device Id가 존재하지 않으면 false 반환")
        void returnFalseIfDeviceIdDoesNotExist() {
            // given
            UUID deviceid = UUID.randomUUID();
            given(refreshTokenCrudService.getByDeviceId(deviceid)).willReturn(Optional.empty());

            // when
            boolean result = tokenValidationService.validateExistedDeviceId(deviceid);

            // then
            assertFalse(result);
        }
    }

    @Nested
    class validateNotFoundMemberUuidTest {
        @Test
        @DisplayName("memberUuid가 없으면 예외 발생")
        void throwIfMemberUuidNotFound() {
            UUID memberUuid = UUID.randomUUID();
            given(memberRepository.findByUuid(memberUuid)).willReturn(Optional.empty());
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundMemberUuid("memberUuid", memberUuid))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("memberUuid가 null이면 예외 발생")
        void throwIfMemberUuidIsNull() {
            assertThatThrownBy(() -> tokenValidationService.validateNotFoundMemberUuid("memberUuid", null))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("memberUuid가 존재하면 예외 없음")
        void passIfMemberExists() {
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            SiteMember member = memberMapper.toSiteMember(memberEntity);
            given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));

            assertThatCode(() -> tokenValidationService.validateNotFoundMemberUuid("memberUuid", member.getUuid()))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateNotFoundTokenUuidTest {
        @Test
        @DisplayName("tokenUuid가 없으면 예외 발생")
        void throwIfTokenNotFound() {
            UUID uuid = UUID.randomUUID();
            given(tokenRepository.findByUuid(uuid)).willReturn(Optional.empty());

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
            given(tokenRepository.findByUuid(tokenEntity.getUuid())).willReturn(Optional.of(tokenEntity));

            assertThatCode(() -> tokenValidationService.validateNotFoundTokenUuid(token.getUuid()))
                    .doesNotThrowAnyException();
        }
    }

}