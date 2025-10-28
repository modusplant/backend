package kr.modusplant.domains.identity.social.framework.out.jpa.mapper;

import kr.modusplant.domains.identity.social.common.util.domain.vo.SocialUserProfileTestUtils;
import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.domains.identity.social.common.util.framework.out.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberRoleEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SocialIdentityJpaMapperImplTest implements MemberEntityTestUtils, SocialUserProfileTestUtils, UserPayloadTestUtils {
    private final SocialIdentityJpaMapper socialIdentityJpaMapper = new SocialIdentityJpaMapperImpl();

    @Test
    @DisplayName("Nickname으로 MemberEntity를 생성")
    void testToMemberEntity_givenNickname_willReturnMemberEntity() {
        // when
        MemberEntity result = socialIdentityJpaMapper.toMemberEntity(testSocialKakaoNickname);

        // then
        assertNotNull(result);
        assertEquals(testSocialKakaoNickname.getNickname(), result.getNickname());
        assertNotNull(result.getLoggedInAt());
    }

    @Test
    @DisplayName("MemberEntity와 SocialUserProfile로 MemberAuthEntity를 생성")
    void testToMemberAuthEntity_givenMemberEntityAndProfile_willReturnMemberAuthEntity() {
        // given
        MemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        String providerId = testKakaoSocialUserProfile.getSocialCredentials().getProviderId();
        String email = testKakaoSocialUserProfile.getEmail().getEmail();

        // when
        MemberAuthEntity result = socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, testKakaoSocialUserProfile);

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
        MemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        Role role = Role.USER;

        // when
        MemberRoleEntity result = socialIdentityJpaMapper.toMemberRoleEntity(memberEntity, role);

        // then
        assertNotNull(result);
        assertEquals(memberEntity, result.getMember());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("MemberEntity와 MemberRoleEntity로 UserPayload를 생성")
    void testToUserPayload_givenMemberEntityAndMemberRoleEntity_willReturnUserPayload() {
        // given
        MemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        MemberRoleEntity memberRoleEntity = MemberRoleEntity.builder()
                .member(memberEntity)
                .role(Role.USER)
                .build();

        // when
        UserPayload result = socialIdentityJpaMapper.toUserPayload(memberEntity, memberRoleEntity);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.getMemberId().getValue());
        assertEquals(memberEntity.getNickname(), result.getNickname().getNickname());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("MemberEntity, Nickname, Role로 UserPayload를 생성")
    void testToUserPayload_givenMemberEntityNicknameAndRole_willReturnUserPayload() {
        // given
        MemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        Role role = Role.USER;

        // when
        UserPayload result = socialIdentityJpaMapper.toUserPayload(memberEntity, testSocialKakaoNickname, role);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.getMemberId().getValue());
        assertEquals(testSocialKakaoNickname.getNickname(), result.getNickname().getNickname());
        assertEquals(Role.USER, result.getRole());
    }


}