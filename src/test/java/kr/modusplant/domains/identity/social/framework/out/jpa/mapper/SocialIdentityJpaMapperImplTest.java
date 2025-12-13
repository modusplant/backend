package kr.modusplant.domains.identity.social.framework.out.jpa.mapper;

import kr.modusplant.domains.identity.social.common.util.domain.vo.SocialUserProfileTestUtils;
import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.domains.identity.social.common.util.framework.out.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocialIdentityJpaMapperImplTest implements MemberEntityTestUtils, SocialUserProfileTestUtils, UserPayloadTestUtils {
    private final SocialIdentityJpaMapper socialIdentityJpaMapper = new SocialIdentityJpaMapperImpl();

    @Test
    @DisplayName("Nickname으로 MemberEntity를 생성")
    void testToMemberEntity_givenNickname_willReturnMemberEntity() {
        // when
        SiteMemberEntity result = socialIdentityJpaMapper.toMemberEntity(testNormalUserNickname);

        // then
        assertNotNull(result);
        assertEquals(testNormalUserNickname.getValue(), result.getNickname());
        assertNotNull(result.getLoggedInAt());
    }

    @Test
    @DisplayName("MemberEntity와 SocialUserProfile로 MemberAuthEntity를 생성")
    void testToMemberAuthEntity_givenMemberEntityAndProfile_willReturnMemberAuthEntity() {
        // given
        SiteMemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        String providerId = testKakaoSocialUserProfile.getSocialCredentials().getProviderId();
        String email = testKakaoSocialUserProfile.getEmail().getValue();

        // when
        SiteMemberAuthEntity result = socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, testKakaoSocialUserProfile);

        // then
        assertNotNull(result);
        assertEquals(memberEntity, result.getActiveMember());
        assertEquals(memberEntity, result.getOriginalMember());
        assertEquals(email, result.getEmail());
        assertEquals(AuthProvider.KAKAO, result.getProvider());
        assertEquals(providerId, result.getProviderId());
    }

    @Test
    @DisplayName("MemberEntity와 Role로 MemberRoleEntity를 생성")
    void testToMemberRoleEntity_givenMemberEntityAndRole_willReturnMemberRoleEntity() {
        // given
        SiteMemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        Role role = Role.USER;

        // when
        SiteMemberRoleEntity result = socialIdentityJpaMapper.toMemberRoleEntity(memberEntity, role);

        // then
        assertNotNull(result);
        assertEquals(memberEntity, result.getMember());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("MemberEntity와 MemberRoleEntity로 UserPayload를 생성")
    void testToUserPayload_givenMemberEntityAndMemberRoleEntity_willReturnUserPayload() {
        // given
        SiteMemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = SiteMemberAuthEntity.builder()
                .activeMember(memberEntity)
                .originalMember(memberEntity)
                .email(testKakaoUserEmail.getValue())
                .build();
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder()
                .member(memberEntity)
                .role(Role.USER)
                .build();

        // when
        UserPayload result = socialIdentityJpaMapper.toUserPayload(memberEntity, memberAuthEntity, memberRoleEntity);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.getMemberId().getValue());
        assertEquals(memberEntity.getNickname(), result.getNickname().getValue());
        assertEquals(memberAuthEntity.getEmail(), result.getEmail().getValue());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("MemberEntity, Nickname, Role로 UserPayload를 생성")
    void testToUserPayload_givenMemberEntityNicknameAndRole_willReturnUserPayload() {
        // given
        SiteMemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        Role role = Role.USER;

        // when
        UserPayload result = socialIdentityJpaMapper.toUserPayload(memberEntity, testNormalUserNickname, testKakaoUserEmail, role);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.getMemberId().getValue());
        assertEquals(testNormalUserNickname.getValue(), result.getNickname().getValue());
        assertEquals(testKakaoUserEmail.getValue(), result.getEmail().getValue());
        assertEquals(Role.USER, result.getRole());
    }


}