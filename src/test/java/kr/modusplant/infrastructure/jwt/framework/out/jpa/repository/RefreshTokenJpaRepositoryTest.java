package kr.modusplant.infrastructure.jwt.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.entity.RefreshTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class RefreshTokenJpaRepositoryTest implements RefreshTokenEntityTestUtils {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository = mock(RefreshTokenJpaRepository.class);
    private final SiteMemberJpaRepository memberJpaRepository = mock(SiteMemberJpaRepository.class);

    private SiteMemberEntity memberEntity;
    private RefreshTokenEntity refreshTokenEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        refreshTokenEntity = createRefreshTokenBasicEntityBuilder()
                .member(memberEntity)
                .build();
    }


    @Test
    @DisplayName("uuid로 refresh token 정보 찾기")
    void testFindByUuid_givenUuid_willReturnRefreshToken() {
        // given
        given(refreshTokenJpaRepository.findByUuid(refreshTokenEntity.getUuid())).willReturn(Optional.of(refreshTokenEntity));

        // when
        Optional<RefreshTokenEntity> result = refreshTokenJpaRepository.findByUuid(refreshTokenEntity.getUuid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshTokenEntity);
    }

    @Test
    @DisplayName("member와 device id로 refresh token 정보 찾기")
    void testFindByMemberAndRefreshToken_givenMemberAndRefreshToken_willReturnRefreshToken() {
        // given
        given(refreshTokenJpaRepository.findByMemberAndRefreshToken(memberEntity, refreshTokenEntity.getRefreshToken()))
                .willReturn(Optional.of(refreshTokenEntity));

        // when
        Optional<RefreshTokenEntity> result = refreshTokenJpaRepository.findByMemberAndRefreshToken(memberEntity, refreshTokenEntity.getRefreshToken());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshTokenEntity);
    }

    @Test
    @DisplayName("refresh token으로 refresh token 정보 찾기")
    void testFindByRefreshToken_givenRefreshToken_willReturnRefreshToken() {
        // given
        given(refreshTokenJpaRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken()))
                .willReturn(Optional.of(refreshTokenEntity));
        // when
        Optional<RefreshTokenEntity> result = refreshTokenJpaRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshTokenEntity);
    }

    @Test
    @DisplayName("uuid로 refresh token 삭제")
    void testDeleteByUuid_givenUuid_willDelete() {
        // given
        UUID uuid = refreshTokenEntity.getUuid();
        willDoNothing().given(refreshTokenJpaRepository).deleteByUuid(uuid);
        given(refreshTokenJpaRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // when
        refreshTokenJpaRepository.deleteByUuid(uuid);

        // then
        Optional<RefreshTokenEntity> result = refreshTokenJpaRepository.findByUuid(uuid);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("uuid로 refresh token 존재 여부 확인")
    void testExistsByUuid_givenUuid_willReturnTrue() {
        // given
        given(refreshTokenJpaRepository.existsByUuid(refreshTokenEntity.getUuid())).willReturn(true);

        // when
        boolean result = refreshTokenJpaRepository.existsByUuid(refreshTokenEntity.getUuid());

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("refresh token값으로 refresh token 정보가 DB에 존재하는지 확인")
    void testExistsByRefreshToken_givenRefreshToken_willReturnTrue() {
        // given
        given(refreshTokenJpaRepository.existsByRefreshToken(refreshTokenEntity.getRefreshToken())).willReturn(true);

        // when
        boolean result = refreshTokenJpaRepository.existsByRefreshToken(refreshTokenEntity.getRefreshToken());

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("만료시간이 지난 refresh token 삭제")
    void testDeleteByExpiredAtBeforeTest_givenExpiredAt_willDelete() {
        // given
        LocalDateTime now = LocalDateTime.now();
        RefreshTokenEntity expiredToken = RefreshTokenEntity.builder()
                .member(memberEntity)
                .refreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3w")
                .issuedAt(now.minusDays(7))
                .expiredAt(now.minusHours(1))
                .build();

        willDoNothing().given(refreshTokenJpaRepository).deleteByExpiredAtBefore(now);
        given(refreshTokenJpaRepository.findAll()).willReturn(List.of(refreshTokenEntity));

        // when
        refreshTokenJpaRepository.deleteByExpiredAtBefore(now);

        // then
        List<RefreshTokenEntity> remainingTokens = refreshTokenJpaRepository.findAll();
        assertThat(remainingTokens.size()).isEqualTo(1);
        assertThat(remainingTokens.get(0).getExpiredAt()).isAfter(now);
    }
}
