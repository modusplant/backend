package kr.modusplant.legacy.modules.jwt.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class RefreshTokenRepositoryTest implements RefreshTokenEntityTestUtils {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    RefreshTokenRepositoryTest(RefreshTokenRepository refreshTokenRepository, SiteMemberRepository memberRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
    }

    @Test
    @DisplayName("uuid로 refresh token 정보 찾기")
    void findByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.findByUuid(refreshToken.getUuid()).orElseThrow()).isEqualTo(refreshToken);

    }

    @Test
    @DisplayName("member와 device id로 refresh token 정보 찾기")
    void findByMemberAndRefreshTokenTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.findByMemberAndRefreshToken(member, refreshToken.getRefreshToken()).orElseThrow()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("refresh token으로 refresh token 정보 찾기")
    void findByRefreshTokenTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.findByRefreshToken(refreshToken.getRefreshToken()).orElseThrow()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("uuid로 refresh token 삭제")
    void deleteByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );
        UUID uuid = refreshToken.getUuid();

        // when
        refreshTokenRepository.deleteByUuid(uuid);

        // then
        assertThat(refreshTokenRepository.findByUuid(uuid)).isEmpty();
    }

    @Test
    @DisplayName("uuid로 refresh token 존재 여부 확인")
    void existsByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.existsByUuid(refreshToken.getUuid())).isEqualTo(true);
    }

    @Test
    @DisplayName("refresh token값으로 refresh token 정보가 DB에 존재하는지 확인")
    void existsByRefreshTokenTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.existsByRefreshToken(refreshToken.getRefreshToken())).isEqualTo(true);
    }

    @Test
    @DisplayName("만료시간이 지난 refresh token 삭제")
    void deleteByExpiredAtBeforeTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .issuedAt(now.minusDays(7))
                        .expiredAt(now.minusHours(1))
                        .build()
        );
        refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .issuedAt(now.minusDays(7))
                        .expiredAt(now.plusHours(1))
                        .build()
        );

        // when
        refreshTokenRepository.deleteByExpiredAtBefore(now);

        // then
        List<RefreshTokenEntity> remainingTokens = refreshTokenRepository.findAll();
        assertThat(remainingTokens.size()).isEqualTo(1);
        assertThat(remainingTokens.get(0).getExpiredAt()).isAfter(now);
    }

}