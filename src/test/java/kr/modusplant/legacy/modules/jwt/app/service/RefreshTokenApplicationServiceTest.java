package kr.modusplant.legacy.modules.jwt.app.service;

import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.legacy.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.legacy.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.legacy.modules.jwt.mapper.RefreshTokenAppInfraMapper;
import kr.modusplant.legacy.modules.jwt.mapper.RefreshTokenAppInfraMapperImpl;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.legacy.modules.jwt.persistence.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class RefreshTokenApplicationServiceTest implements RefreshTokenTestUtils, RefreshTokenEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils, SiteMemberRequestTestUtils {

    @InjectMocks
    private RefreshTokenApplicationService tokenApplicationService;

    @Mock
    private SiteMemberApplicationService memberService;

    @Mock
    private RefreshTokenRepository tokenRepository;

    @Mock
    private SiteMemberRepository memberRepository;

    @Spy
    private final RefreshTokenAppInfraMapper tokenMapper = new RefreshTokenAppInfraMapperImpl();

    @Test
    @DisplayName("uuid로 refresh token 조회 테스트")
    void getByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        given(tokenRepository.findByUuid(tokenEntity.getUuid())).willReturn(Optional.of(tokenEntity));
        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        token = tokenApplicationService.insert(token);

        // then
        assertThat(tokenApplicationService.getByUuid(token.getUuid()).orElseThrow()).isEqualTo(token);
    }

    @Test
    @DisplayName("MemberUuid와 DeviceId로 Refresh Token 조회 테스트")
    void getByMemberUuidAndRefreshTokenTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(tokenRepository.findByMemberAndRefreshToken(memberEntity, tokenEntity.getRefreshToken())).willReturn(Optional.of(tokenEntity));
        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        token = tokenApplicationService.insert(token);

        // then
        assertThat(tokenApplicationService.getByMemberUuidAndRefreshToken(token.getMemberUuid(), token.getRefreshToken()).orElseThrow()).isEqualTo(token);
    }

    @Test
    @DisplayName("Refresh Token으로 Refresh Token 정보 조회 테스트")
    void getByRefreshTokenTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        given(tokenRepository.findByRefreshToken(tokenEntity.getRefreshToken())).willReturn(Optional.of(tokenEntity));
        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        token = tokenApplicationService.insert(token);

        // then
        assertThat(tokenApplicationService.getByRefreshToken(token.getRefreshToken()).orElseThrow()).isEqualTo(token);
    }

    @Test
    @DisplayName("빈 refresh token 얻기")
    void getOptionalEmptyTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        // getByUuid
        // given
        given(tokenRepository.findByRefreshToken(token.getRefreshToken())).willReturn(Optional.empty());
        // then
        assertThat(tokenApplicationService.getByUuid(token.getUuid())).isEmpty();

        // getByMemberUuidAndRefreshToken
        // given
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(tokenRepository.findByMemberAndRefreshToken(memberEntity, tokenEntity.getRefreshToken())).willReturn(Optional.empty());
        //then
        assertThat(tokenApplicationService.getByMemberUuidAndRefreshToken(memberEntity.getUuid(), token.getRefreshToken())).isEmpty();

        // getByRefreshToken
        // given
        given(tokenRepository.findByRefreshToken(tokenEntity.getRefreshToken())).willReturn(Optional.empty());
        // then
        assertThat(tokenApplicationService.getByRefreshToken(token.getRefreshToken())).isEmpty();
    }

    @Test
    @DisplayName("refresh token 저장 테스트")
    void insertTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        RefreshToken result = tokenApplicationService.insert(token);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(token);
    }


    @Test
    @DisplayName("uuid로 refresh token 제거 테스트")
    void removeByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);
        UUID uuid = token.getUuid();

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);
        willDoNothing().given(tokenRepository).deleteByUuid(tokenEntity.getUuid());

        // when
        memberService.insert(memberBasicUserInsertRequest);
        tokenApplicationService.insert(token);
        tokenApplicationService.removeByUuid(uuid);

        // then
        assertThat(tokenApplicationService.getByUuid(uuid)).isEmpty();
    }
}