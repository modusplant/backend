package kr.modusplant.domains.account.social.framework.out.jpa.mapper;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialAccountPayloadTestUtils;
import kr.modusplant.domains.account.social.common.util.domain.vo.SocialAccountProfileTestUtils;
import kr.modusplant.domains.account.social.common.util.framework.out.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocialIdentityJpaMapperImplTest implements MemberEntityTestUtils, SocialAccountProfileTestUtils, SocialAccountPayloadTestUtils {
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
        String providerId = TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE.getSocialCredentials().getProviderId();
        String email = TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE.getEmail().getValue();

        // when
        SiteMemberAuthEntity result = socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE);

        // then
        assertNotNull(result);
        assertEquals(memberEntity, result.getMember());
        assertEquals(email, result.getEmail());
        assertEquals(AuthProvider.KAKAO, result.getProvider());
        assertEquals(providerId, result.getProviderId());
    }

    @Test
    @DisplayName("MemberEntity로 UserPayload를 생성")
    void testToUserPayload_givenMemberEntity_willReturnUserPayload() {
        // given
        SiteMemberEntity memberEntity = createKakaoMemberEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = SiteMemberAuthEntity.builder()
                .member(memberEntity)
                .email(testKakaoUserEmail.getValue())
                .build();

        // when
        SocialAccountPayload result = socialIdentityJpaMapper.toUserPayload(memberEntity, memberAuthEntity);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.getAccountId().getValue());
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
        SocialAccountPayload result = socialIdentityJpaMapper.toUserPayload(memberEntity, testNormalUserNickname, testKakaoUserEmail, role);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.getAccountId().getValue());
        assertEquals(testNormalUserNickname.getValue(), result.getNickname().getValue());
        assertEquals(testKakaoUserEmail.getValue(), result.getEmail().getValue());
        assertEquals(Role.USER, result.getRole());
    }


}