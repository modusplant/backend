package kr.modusplant.modules.auth.social.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.mapper.SiteMemberAuthDomainInfraMapper;
import kr.modusplant.domains.member.mapper.SiteMemberAuthDomainInfraMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.auth.social.app.dto.GoogleUserInfo;
import kr.modusplant.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.modules.auth.social.app.dto.KakaoUserInfo;
import kr.modusplant.modules.auth.social.app.dto.supers.SocialUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DomainsServiceOnlyContext
class SocialAuthApplicationServiceTest implements SiteMemberEntityTestUtils, SiteMemberAuthEntityTestUtils {

    private SocialAuthApplicationService socialAuthApplicationService;
    @Mock
    private KakaoAuthClient kakaoAuthClient;
    @Mock
    private GoogleAuthClient googleAuthClient;
    @Mock
    private SiteMemberRepository memberRepository;
    @Mock
    private SiteMemberAuthRepository memberAuthRepository;
    @Mock
    private SiteMemberRoleRepository memberRoleRepository;

    private final SiteMemberAuthDomainInfraMapper memberAuthEntityMapper = new SiteMemberAuthDomainInfraMapperImpl();

    private final String code = "sample-code";
    private final AuthProvider provider = AuthProvider.GOOGLE;
    private final String id = "639796866968871286823";
    private final String email = "Test3gOogleUsser@gmail.com";
    private final String nickname = "구글 유저";

    @BeforeEach
    void setUp() {
        socialAuthApplicationService = spy(new SocialAuthApplicationService(
                kakaoAuthClient, googleAuthClient,
                memberRepository, memberAuthRepository, memberRoleRepository
                )
        );
    }

    @Test
    @DisplayName("카카오 소셜 로그인 성공 테스트")
    void handleSocialLoginKakaoSuccessTest() {
        // given
        String kakaoAccessToken = "kakao-access-token";
        SocialUserInfo userInfo = mock(KakaoUserInfo.class);
        given(userInfo.getId()).willReturn("kakao-id");
        given(userInfo.getEmail()).willReturn("test@kakao.com");
        given(userInfo.getNickname()).willReturn("kakao-nickname");
        JwtUserPayload jwtUserPayload = new JwtUserPayload(UUID.randomUUID(),"kakao-nickname",Role.USER);

        given(kakaoAuthClient.getAccessToken(code)).willReturn(kakaoAccessToken);
        given(kakaoAuthClient.getUserInfo(kakaoAccessToken)).willReturn((KakaoUserInfo) userInfo);
        doReturn(jwtUserPayload).when(socialAuthApplicationService)
                .findOrCreateMember(eq(AuthProvider.KAKAO),any(String.class),any(String.class),any(String.class));

        // when
        JwtUserPayload result = socialAuthApplicationService.handleSocialLogin(AuthProvider.KAKAO,code);

        // then
        assertThat(result).isEqualTo(jwtUserPayload);
    }

    @Test
    @DisplayName("구글 소셜 로그인 성공 테스트")
    void handleSocialLoginGoogleSuccessTest() {
        // given
        String googleAccessToken = "google-access-token";
        SocialUserInfo userInfo = mock(GoogleUserInfo.class);
        given(userInfo.getId()).willReturn("google-id");
        given(userInfo.getEmail()).willReturn("test@gmail.com");
        given(userInfo.getNickname()).willReturn("google-nickname");
        JwtUserPayload jwtUserPayload = new JwtUserPayload(UUID.randomUUID(),"google-nickname",Role.USER);

        given(googleAuthClient.getAccessToken(code)).willReturn(googleAccessToken);
        given(googleAuthClient.getUserInfo(googleAccessToken)).willReturn((GoogleUserInfo) userInfo);
        doReturn(jwtUserPayload).when(socialAuthApplicationService)
                .findOrCreateMember(eq(AuthProvider.GOOGLE),any(String.class),any(String.class),any(String.class));

        // when
        JwtUserPayload result = socialAuthApplicationService.handleSocialLogin(AuthProvider.GOOGLE,code);

        // then
        assertThat(result).isEqualTo(jwtUserPayload);
    }

    @Test
    @DisplayName("지원하지 않는 소셜 로그인 테스트")
    void handleSocialLoginUnsupportedProviderTest() {
        // when & then
        assertThatThrownBy(() -> socialAuthApplicationService.handleSocialLogin(AuthProvider.BASIC, code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이 방법은 지원되지 않습니다.");
    }

    @Test
    @DisplayName("이미 존재하는 사용자라면, 사용자 정보를 조회한다")
    void findOrCreateMemberWhenMemberExists() {
        // Given
        SiteMemberEntity memberEntity = createMemberGoogleUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthGoogleUserEntityBuilder()
                .activeMember(memberEntity)
                .originalMember(memberEntity)
                .build();
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder()
                .member(memberEntity)
                .role(Role.USER).build();

        given(memberAuthRepository.findByProviderAndProviderId(provider,id)).willReturn(Optional.of(memberAuthEntity));
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(any())).willReturn(memberEntity);
        given(memberRoleRepository.findByMember(memberEntity)).willReturn(Optional.of(memberRoleEntity));

        // when
        JwtUserPayload result = socialAuthApplicationService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.memberUuid());
        assertEquals(memberAuthEntity.getOriginalMember().getUuid(), result.memberUuid());
        assertEquals(memberRoleEntity.getMember().getUuid(), result.memberUuid());
    }

    @Test
    @DisplayName("존재하지 않는 사용자라면, 사용자를 생성한다")
    void findOrCreateMemberWhenMemberDoesNotExists() {
        // given
        SiteMemberEntity newMemberEntity = createMemberGoogleUserEntityWithUuid();
        SiteMemberRoleEntity newMemberRoleEntity = SiteMemberRoleEntity.builder()
                .member(newMemberEntity)
                .role(Role.USER).build();

        given(memberAuthRepository.findByProviderAndProviderId(provider,id)).willReturn(Optional.empty());
        given(memberRepository.save(any(SiteMemberEntity.class))).willReturn(newMemberEntity);
        given(memberAuthRepository.save(any(SiteMemberAuthEntity.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(memberRoleRepository.save(any(SiteMemberRoleEntity.class))).willReturn(newMemberRoleEntity);

        // When
        JwtUserPayload result = socialAuthApplicationService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.memberUuid()).isEqualTo(newMemberEntity.getUuid());
        assertThat(result.nickname()).isEqualTo(nickname);
        assertThat(result.role()).isEqualTo(newMemberRoleEntity.getRole());
    }
}