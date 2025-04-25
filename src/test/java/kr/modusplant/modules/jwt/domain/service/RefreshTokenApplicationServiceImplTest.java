package kr.modusplant.modules.jwt.domain.service;

import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.modules.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.mapper.entity.RefreshTokenEntityMapper;
import kr.modusplant.modules.jwt.mapper.entity.RefreshTokenEntityMapperImpl;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenJpaRepository;
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
class RefreshTokenApplicationServiceImplTest implements RefreshTokenTestUtils, RefreshTokenEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils, SiteMemberRequestTestUtils {

    @InjectMocks
    private RefreshTokenApplicationServiceImpl tokenApplicationService;

    @Mock
    private SiteMemberApplicationService memberService;

    @Mock
    private RefreshTokenJpaRepository tokenRepository;

    @Mock
    private SiteMemberRepository memberRepository;

    @Spy
    private final RefreshTokenEntityMapper tokenMapper = new RefreshTokenEntityMapperImpl();

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
    void getByMemberUuidAndDeviceIdTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(tokenRepository.findByMemberAndDeviceId(memberEntity, tokenEntity.getDeviceId())).willReturn(Optional.of(tokenEntity));
        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        token = tokenApplicationService.insert(token);

        // then
        assertThat(tokenApplicationService.getByMemberUuidAndDeviceId(token.getMemberUuid(), token.getDeviceId()).orElseThrow()).isEqualTo(token);
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
    @DisplayName("DeviceId로 Refresh Token 조회 테스트")
    void getByDeviceIdTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        RefreshTokenEntity tokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        RefreshToken token = tokenMapper.toRefreshToken(tokenEntity);

        given(tokenRepository.findByDeviceId(tokenEntity.getDeviceId())).willReturn(Optional.of(tokenEntity));
        given(tokenRepository.save(tokenEntity)).willReturn(tokenEntity);
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        token = tokenApplicationService.insert(token);

        // then
        assertThat(tokenApplicationService.getByDeviceId(token.getDeviceId()).orElseThrow()).isEqualTo(token);
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

        // getByMemberUuidAndDeviceId
        // given
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(tokenRepository.findByMemberAndDeviceId(memberEntity, tokenEntity.getDeviceId())).willReturn(Optional.empty());
        //then
        assertThat(tokenApplicationService.getByMemberUuidAndDeviceId(memberEntity.getUuid(), token.getDeviceId())).isEmpty();

        // getByRefreshToken
        // given
        given(tokenRepository.findByRefreshToken(tokenEntity.getRefreshToken())).willReturn(Optional.empty());
        // then
        assertThat(tokenApplicationService.getByRefreshToken(token.getRefreshToken())).isEmpty();

        // getByDeviceId
        // given
        given(tokenRepository.findByDeviceId(tokenEntity.getDeviceId())).willReturn(Optional.empty());
        // then
        assertThat(tokenApplicationService.getByDeviceId(token.getDeviceId())).isEmpty();
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