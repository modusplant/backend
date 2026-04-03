package kr.modusplant.domains.account.social.framework.out.jpa.repository;

import kr.modusplant.domains.account.social.common.util.domain.vo.AgreedTermsTestUtils;
import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberAuthEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberProfileEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberTermJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class SocialIdentityRepositoryJpaAdapterTest implements SocialMemberProfileTestUtils, AgreedTermsTestUtils, SiteMemberEntityTestUtils, SiteMemberAuthEntityTestUtils, SiteMemberTermEntityTestUtils, SiteMemberProfileEntityTestUtils {
    private final SiteMemberJpaRepository memberJpaRepository = mock(SiteMemberJpaRepository.class);
    private final SiteMemberAuthJpaRepository memberAuthJpaRepository = mock(SiteMemberAuthJpaRepository.class);
    private final SiteMemberProfileJpaRepository memberProfileJpaRepository = mock(SiteMemberProfileJpaRepository.class);
    private final SiteMemberTermJpaRepository memberTermJpaRepository = mock(SiteMemberTermJpaRepository.class);
    private final SocialIdentityJpaMapper socialIdentityJpaMapper = mock(SocialIdentityJpaMapper.class);
    private final SocialIdentityRepositoryJpaAdapter socialIdentityRepositoryJpaAdapter = new SocialIdentityRepositoryJpaAdapter(
            memberJpaRepository, memberAuthJpaRepository, memberProfileJpaRepository, memberTermJpaRepository, socialIdentityJpaMapper
    );

    private SiteMemberEntity basicMemberEntity;
    private SiteMemberEntity kakaoMemberEntity;
    private SiteMemberAuthEntity basicMemberAuthEntity;
    private SiteMemberAuthEntity kakaoMemberAuthEntity;

    @BeforeEach
    void setUp() {
        basicMemberEntity = createMemberBasicUserEntityWithUuid();
        kakaoMemberEntity = createMemberKakaoUserEntityWithUuid();
        basicMemberAuthEntity = createMemberAuthBasicUserEntityBuilder()
                .member(basicMemberEntity)
                .build();
        kakaoMemberAuthEntity = createMemberAuthKakaoUserEntityBuilder()
                .member(kakaoMemberEntity)
                .build();
    }

    @Test
    @DisplayName("이메일로 회원 조회 시 SocialMemberProfile을 반환")
    void testGetSocialMemberProfileByEmail_givenEmail_willReturnOptionalSocialMemberProfile() {
        // given
        given(memberAuthJpaRepository.findByEmail(testNormalUserEmail.getValue())).willReturn(Optional.of(basicMemberAuthEntity));
        given(socialIdentityJpaMapper.toSocialMemberProfile(basicMemberEntity, basicMemberAuthEntity)).willReturn(testBasicSocialMemberProfile);

        // when
        Optional<SocialMemberProfile> result = socialIdentityRepositoryJpaAdapter.getSocialMemberProfileByEmail(testNormalUserEmail);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBasicSocialMemberProfile);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional을 반환")
    void testGetSocialMemberProfileByEmail_givenEmail_willReturnOptional() {
        // given
        given(memberAuthJpaRepository.findByEmail(testNormalUserEmail.getValue())).willReturn(Optional.empty());

        // when
        Optional<SocialMemberProfile> result = socialIdentityRepositoryJpaAdapter.getSocialMemberProfileByEmail(testNormalUserEmail);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("로그인 시각 업데이트 후 SocialMemberProfile을 반환")
    void testUpdateLoggedInAtAndGetProfile_givenAccountId_willReturnSocialMemberProfile() {
        // given
        given(memberJpaRepository.findByUuid(testKakaoAccountId.getValue())).willReturn(Optional.of(kakaoMemberEntity));
        given(memberJpaRepository.save(kakaoMemberEntity)).willReturn(kakaoMemberEntity);
        given(memberAuthJpaRepository.findByMember(kakaoMemberEntity)).willReturn(Optional.of(kakaoMemberAuthEntity));
        given(socialIdentityJpaMapper.toSocialMemberProfile(kakaoMemberEntity, kakaoMemberAuthEntity)).willReturn(testKakaoSocialMemberProfile);

        // when
        SocialMemberProfile result = socialIdentityRepositoryJpaAdapter.updateLoggedInAtAndGetProfile(testKakaoAccountId);

        // then
        assertThat(result).isEqualTo(testKakaoSocialMemberProfile);
        verify(memberJpaRepository).save(kakaoMemberEntity);
    }

    @Test
    @DisplayName("신규 소셜 회원 저장 시 SocialMemberProfile을 반환")
    void testSaveSocialMember_givenSocialMemberProfileAndIntroductionAndAgreedTerms_willReturnSocialMemberProfile() {
        // given
        SiteMemberProfileEntity memberProfileEntity = createMemberProfileBasicUserEntityBuilder()
                .member(kakaoMemberEntity)
                .build();
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntity();

        given(socialIdentityJpaMapper.toMemberEntity(testKakaoSocialMemberProfile.getNickname(), testKakaoSocialMemberProfile.getRole())).willReturn(kakaoMemberEntity);
        given(memberJpaRepository.save(kakaoMemberEntity)).willReturn(kakaoMemberEntity);
        given(socialIdentityJpaMapper.toMemberAuthEntity(kakaoMemberEntity, testKakaoSocialMemberProfile.getSocialCredentials(), testKakaoSocialMemberProfile.getEmail()))
                .willReturn(kakaoMemberAuthEntity);
        given(memberAuthJpaRepository.save(kakaoMemberAuthEntity)).willReturn(kakaoMemberAuthEntity);
        given(socialIdentityJpaMapper.toMemberProfileEntity(eq(kakaoMemberEntity), any(String.class))).willReturn(memberProfileEntity);
        given(socialIdentityJpaMapper.toMemberTermEntity(kakaoMemberEntity, testAgreedTerms)).willReturn(memberTermEntity);
        given(socialIdentityJpaMapper.toSocialMemberProfile(kakaoMemberEntity, kakaoMemberAuthEntity)).willReturn(testKakaoSocialMemberProfile);

        // when
        SocialMemberProfile result = socialIdentityRepositoryJpaAdapter.saveSocialMember(testKakaoSocialMemberProfile, "소개글", testAgreedTerms);

        // then
        assertThat(result).isEqualTo(testKakaoSocialMemberProfile);
        verify(memberJpaRepository).save(kakaoMemberEntity);
        verify(memberAuthJpaRepository).save(kakaoMemberAuthEntity);
        verify(memberProfileJpaRepository).save(memberProfileEntity);
        verify(memberTermJpaRepository).save(memberTermEntity);
    }

    @Test
    @DisplayName("일반 계정에 카카오 소셜 연동 시 SocialMemberProfile을 반환")
    void testUpdateSocialLinkedMember_givenSocialCredentialsAndEmail_willReturnSocialMemberProfile() {
        // given
        given(memberAuthJpaRepository.findByEmail(testNormalUserEmail.getValue())).willReturn(Optional.of(basicMemberAuthEntity));
        given(memberAuthJpaRepository.save(basicMemberAuthEntity)).willReturn(basicMemberAuthEntity);
        given(memberJpaRepository.findByUuid(basicMemberAuthEntity.getUuid())).willReturn(Optional.of(basicMemberEntity));
        given(memberJpaRepository.save(basicMemberEntity)).willReturn(basicMemberEntity);
        given(socialIdentityJpaMapper.toSocialMemberProfile(basicMemberEntity, basicMemberAuthEntity)).willReturn(testBasicKakaoSocialMemberProfile);

        // when
        SocialMemberProfile result = socialIdentityRepositoryJpaAdapter.updateSocialLinkedMember(testBasicKakaoSocialCredentials, testNormalUserEmail);

        // then
        assertThat(result).isEqualTo(testBasicKakaoSocialMemberProfile);
        verify(memberAuthJpaRepository).save(basicMemberAuthEntity);
        verify(memberJpaRepository).save(basicMemberEntity);
    }


}