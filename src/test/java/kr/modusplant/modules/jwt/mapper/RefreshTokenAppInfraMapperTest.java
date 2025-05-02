package kr.modusplant.modules.jwt.mapper;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class RefreshTokenAppInfraMapperTest implements RefreshTokenTestUtils, RefreshTokenEntityTestUtils {

    private final SiteMemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenAppInfraMapper refreshTokenMapper = new RefreshTokenAppInfraMapperImpl();

    @Autowired
    RefreshTokenAppInfraMapperTest(SiteMemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Test
    @DisplayName("매퍼 적용 후 일관된 refresh token 엔티티 확인")
    void checkConsistentEntity() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());

        // when
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(memberEntity)
                        .build()
        );

        // then
        assertThat(refreshTokenEntity).isEqualTo(refreshTokenMapper.toRefreshTokenEntity(refreshTokenMapper.toRefreshToken(refreshTokenEntity), memberRepository));
    }

    @Test
    @DisplayName("매퍼 적용 후 일관된 refresh token 도메인 확인")
    void checkConsistentDomain() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.save(
                createRefreshTokenBasicEntityBuilder()
                        .member(memberEntity)
                        .build()
        );

        // when
        RefreshToken refreshToken = refreshTokenMapper.toRefreshToken(refreshTokenEntity);

        // then
        assertThat(refreshToken)
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(refreshTokenMapper.toRefreshToken(refreshTokenMapper.toRefreshTokenEntity(refreshToken, memberRepository)));
    }
}