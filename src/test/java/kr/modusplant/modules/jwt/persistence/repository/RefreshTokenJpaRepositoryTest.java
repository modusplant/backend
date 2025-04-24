package kr.modusplant.modules.jwt.persistence.repository;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class RefreshTokenJpaRepositoryTest implements RefreshTokenEntityTestUtils {

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    RefreshTokenJpaRepositoryTest(RefreshTokenJpaRepository refreshTokenRepository, SiteMemberRepository memberRepository) {
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
    void findByMemberAndDeviceIdTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.findByMemberAndDeviceId(member, refreshToken.getDeviceId()).orElseThrow()).isEqualTo(refreshToken);
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
    @DisplayName("deviceId로 refresh token 정보 찾기")
    void findByDeviceIdTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(member)
                        .build()
        );

        // then
        assertThat(refreshTokenRepository.findByDeviceId(refreshToken.getDeviceId()).orElseThrow()).isEqualTo(refreshToken);
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

}