package kr.modusplant.domains.identity.social.framework.out.jpa.repository;

import kr.modusplant.domains.identity.social.common.util.domain.vo.SocialCredentialsTestUtils;
import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.domains.identity.social.domain.vo.MemberId;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.persistence.constant.TableName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_KAKAO_MEMBER_ID_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SocialIdentityRepositoryJpaAdapterTest implements SocialCredentialsTestUtils, UserPayloadTestUtils, EmailTestUtils {
    private final SiteMemberJpaRepository memberJpaRepository = mock(SiteMemberJpaRepository.class);
    private final SiteMemberAuthJpaRepository memberAuthJpaRepository = mock(SiteMemberAuthJpaRepository.class);
    private final SiteMemberRoleJpaRepository memberRoleJpaRepository = mock(SiteMemberRoleJpaRepository.class);
    private final SocialIdentityJpaMapper socialIdentityJpaMapper = mock(SocialIdentityJpaMapper.class);
    private final SocialIdentityRepositoryJpaAdapter socialIdentityRepositoryJpaAdapter = new SocialIdentityRepositoryJpaAdapter(
            memberJpaRepository,memberAuthJpaRepository,memberRoleJpaRepository,socialIdentityJpaMapper
    );

    @Test
    @DisplayName("유효한 SocialCredentials로 MemberId를 조회")
    void testGetMemberIdBySocialCredentials_givenValidCredentials_willReturnMemberId() {
        // given
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        SiteMemberAuthEntity memberAuthEntity = mock(SiteMemberAuthEntity.class);

        given(memberAuthEntity.getActiveMember()).willReturn(memberEntity);
        given(memberEntity.getUuid()).willReturn(TEST_SOCIAL_KAKAO_MEMBER_ID_UUID);
        given((memberAuthJpaRepository.findByProviderAndProviderId(
                testKakaoSocialCredentials.getProvider(),
                testKakaoSocialCredentials.getProviderId()
        ))).willReturn(Optional.of(memberAuthEntity));

        // when
        Optional<MemberId> result = socialIdentityRepositoryJpaAdapter.getMemberIdBySocialCredentials(testKakaoSocialCredentials);

        // then
        assertTrue(result.isPresent());
        assertEquals(TEST_SOCIAL_KAKAO_MEMBER_ID_UUID, result.get().getValue());
        verify(memberAuthJpaRepository).findByProviderAndProviderId(testKakaoSocialCredentials.getProvider(), testKakaoSocialCredentials.getProviderId());
    }

    @Test
    @DisplayName("존재하지 않는 SocialCredentials로 조회 시 빈 Optional을 반환")
    void testGetMemberIdBySocialCredentials_givenNonExistentCredentials_willReturnEmpty() {
        // given
        given(memberAuthJpaRepository.findByProviderAndProviderId(
                testKakaoSocialCredentials.getProvider(), testKakaoSocialCredentials.getProviderId()
        )).willReturn(Optional.empty());

        // when
        Optional<MemberId> result = socialIdentityRepositoryJpaAdapter.getMemberIdBySocialCredentials(testKakaoSocialCredentials);

        // then
        assertFalse(result.isPresent());
        verify(memberAuthJpaRepository).findByProviderAndProviderId(testKakaoSocialCredentials.getProvider(), testKakaoSocialCredentials.getProviderId());
    }

    @Test
    @DisplayName("유효한 MemberId로 UserPayload를 조회")
    void testGetUserPayloadByMemberId_givenValidMemberId_willReturnUserPayload() {
        // given
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        SiteMemberRoleEntity memberRoleEntity = mock(SiteMemberRoleEntity.class);

        given(memberJpaRepository.findByUuid(testSocialKakaoMemberId.getValue())).willReturn(Optional.of(memberEntity));
        given(memberRoleJpaRepository.findByMember(memberEntity)).willReturn(Optional.of(memberRoleEntity));
        given(socialIdentityJpaMapper.toUserPayload(memberEntity, memberRoleEntity)).willReturn(testSocialKakaoUserPayload);


        // when
        UserPayload result = socialIdentityRepositoryJpaAdapter.getUserPayloadByMemberId(testSocialKakaoMemberId);

        // then
        assertNotNull(result);
        assertEquals(testSocialKakaoUserPayload, result);
        verify(memberJpaRepository).findByUuid(testSocialKakaoMemberId.getValue());
        verify(memberRoleJpaRepository).findByMember(memberEntity);
        verify(socialIdentityJpaMapper).toUserPayload(memberEntity, memberRoleEntity);
    }

    @Test
    @DisplayName("존재하지 않는 MemberId로 조회 시 예외 발생")
    void testGetUserPayloadByMemberId_givenNonExistentMemberId_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(testSocialKakaoMemberId.getValue())).willReturn(Optional.empty());

        // when & then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> socialIdentityRepositoryJpaAdapter.getUserPayloadByMemberId(testSocialKakaoMemberId));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        assertThat(exception.getEntityName()).isEqualTo(TableName.SITE_MEMBER);
        verify(memberJpaRepository).findByUuid(testSocialKakaoMemberId.getValue());
    }

    @Test
    @DisplayName("MemberRole이 존재하지 않을 때 예외 발생")
    void testGetUserPayloadByMemberId_givenMemberWithoutRole_willThrowException() {
        // given
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);

        given(memberJpaRepository.findByUuid(testSocialKakaoMemberId.getValue())).willReturn(Optional.of(memberEntity));
        given(memberRoleJpaRepository.findByMember(memberEntity)).willReturn(Optional.empty());

        // when & then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> socialIdentityRepositoryJpaAdapter.getUserPayloadByMemberId(testSocialKakaoMemberId));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_ROLE_NOT_FOUND);
        assertThat(exception.getEntityName()).isEqualTo(TableName.SITE_MEMBER_ROLE);
        verify(memberRoleJpaRepository).findByMember(memberEntity);
    }

    @Test
    @DisplayName("유효한 MemberId로 로그인 시간을 업데이트")
    void testUpdateLoggedInAt_givenValidMemberId_willUpdateLoggedInAt() {
        // given
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        given(memberJpaRepository.findByUuid(testSocialKakaoMemberId.getValue())).willReturn(Optional.of(memberEntity));
        given(memberJpaRepository.save(memberEntity)).willReturn(memberEntity);

        // when
        socialIdentityRepositoryJpaAdapter.updateLoggedInAt(testSocialKakaoMemberId);

        // then
        verify(memberJpaRepository).findByUuid(testSocialKakaoMemberId.getValue());
        verify(memberEntity).updateLoggedInAt(any(LocalDateTime.class));
        verify(memberJpaRepository).save(memberEntity);
    }

    @Test
    @DisplayName("유효한 SocialUserProfile과 Role로 소셜 회원을 생성")
    void testCreateSocialMember_givenValidProfileAndRole_willCreateMemberAndReturnPayload() {
        // given
        SocialUserProfile profile = SocialUserProfile.create(testKakaoSocialCredentials, testKakaoUserEmail, testSocialKakaoNickname);
        Role role = Role.USER;
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        SiteMemberAuthEntity memberAuthEntity = mock(SiteMemberAuthEntity.class);
        SiteMemberRoleEntity memberRoleEntity = mock(SiteMemberRoleEntity.class);

        given(socialIdentityJpaMapper.toMemberEntity(testSocialKakaoNickname)).willReturn(memberEntity);
        given(memberJpaRepository.save(memberEntity)).willReturn(memberEntity);
        given(socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, profile)).willReturn(memberAuthEntity);
        given(memberAuthJpaRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(socialIdentityJpaMapper.toMemberRoleEntity(memberEntity, role)).willReturn(memberRoleEntity);
        given(memberRoleJpaRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(socialIdentityJpaMapper.toUserPayload(memberEntity, testSocialKakaoNickname, role)).willReturn(testSocialKakaoUserPayload);

        // when
        UserPayload result = socialIdentityRepositoryJpaAdapter.createSocialMember(profile, role);

        // then
        assertNotNull(result);
        assertEquals(testSocialKakaoUserPayload, result);
        verify(socialIdentityJpaMapper).toMemberEntity(testSocialKakaoNickname);
        verify(memberJpaRepository).save(memberEntity);
        verify(socialIdentityJpaMapper).toMemberAuthEntity(memberEntity, profile);
        verify(memberAuthJpaRepository).save(memberAuthEntity);
        verify(socialIdentityJpaMapper).toMemberRoleEntity(memberEntity, role);
        verify(memberRoleJpaRepository).save(memberRoleEntity);
        verify(socialIdentityJpaMapper).toUserPayload(memberEntity, testSocialKakaoNickname, role);
    }

}