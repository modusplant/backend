package kr.modusplant.domains.account.social.framework.outbound.jpa.mapper;

import kr.modusplant.domains.account.identity.common.util.framework.outbound.jpa.entity.MemberAuthEntityTestUtils;
import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.social.common.util.domain.vo.AgreedTermsTestUtils;
import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.framework.outbound.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberProfileEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.term.common.util.framework.outbound.jpa.entity.MemberTermEntityTestUtils;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import kr.modusplant.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocialIdentityJpaMapperImplTest implements MemberEntityTestUtils, MemberAuthEntityTestUtils, MemberProfileEntityTestUtils, MemberTermEntityTestUtils,
        SocialMemberProfileTestUtils, AgreedTermsTestUtils {
    private final SocialIdentityJpaMapper socialIdentityJpaMapper = new SocialIdentityJpaMapperImpl();

    @Test
    @DisplayName("Nickname으로 MemberEntity를 생성")
    void testToMemberEntity_givenNickname_willReturnMemberEntity() {
        // when
        MemberEntity result = socialIdentityJpaMapper.toMemberEntity(testGoogleUserNickname,Role.USER);

        // then
        assertNotNull(result);
        assertEquals(testGoogleUserNickname.getValue(), result.getNickname());
        assertEquals(Role.USER, result.getRole());
        assertNotNull(result.getLoggedInAt());
    }

    @Test
    @DisplayName("MemberEntity와 SocialCredentials, Email로 MemberAuthEntity를 생성")
    void testToMemberAuthEntity_givenMemberEntityAndSocialCredentialsAndEmail_willReturnMemberAuthEntity() {
        // given
        MemberEntity memberEntity = createMemberGoogleUserEntityWithUuid();

        // when
        MemberAuthEntity result = socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, testGoogleSocialCredentials, testGoogleUserEmail);

        // then
        assertNotNull(result);
        assertEquals(memberEntity, result.getMember());
        assertEquals(testGoogleUserEmail.getValue(), result.getEmail());
        assertEquals(testGoogleSocialCredentials.getProvider(), result.getProvider());
        assertEquals(testGoogleSocialCredentials.getProviderId(), result.getProviderId());
    }

    @Test
    @DisplayName("MemberEntity와 AgreedTerms로 MemberTermEntity를 생성")
    void testToMemberTermEntity_givenMemberEntityAndAgreedTerms_willReturnSiteMemberTermEntity() {
        // given
        MemberEntity memberEntity = createMemberGoogleUserEntityWithUuid();

        // when
        MemberTermEntity result = socialIdentityJpaMapper.toMemberTermEntity(memberEntity,testAgreedTerms);

        // then
        assertNotNull(result);
        assertEquals(memberEntity,result.getMember());
        assertEquals(testAgreedTerms.getAgreedTermsOfUseVersion().getValue(),result.getAgreedTermsOfUseVersion());
        assertEquals(testAgreedTerms.getAgreedPrivacyPolicyVersion().getValue(),result.getAgreedPrivacyPolicyVersion());
        assertEquals(testAgreedTerms.getAgreedCommunityPolicyVersion().getValue(),result.getAgreedCommunityPolicyVersion());
    }

    @Test
    @DisplayName("MemberEntity와 introduction으로 MemberProfileEntity를 생성")
    void testToMemberProfileEntity_givenMemberEntityAndIntroduction_willReturnMemberProfileEntity() {
        // given
        MemberEntity memberEntity = createMemberGoogleUserEntityWithUuid();
        String introduction = "프로필 소개";

        // when
        MemberProfileEntity result = socialIdentityJpaMapper.toMemberProfileEntity(memberEntity,introduction);

        // then
        assertNotNull(result);
        assertEquals(memberEntity,result.getMember());
        assertEquals(introduction,result.getIntroduction());
    }

    @Test
    @DisplayName("MemberEntity와 MemberAuthEntity로 SocialMemberProfile를 생성")
    void testToSocialMemberProfile_givenMemberEntityAndMemberAuthEntity_willReturnSocialMemberProfile() {
        // given
        MemberEntity memberEntity = createMemberGoogleUserEntityWithUuid();
        MemberAuthEntity memberAuthEntity = createMemberAuthGoogleUserEntityBuilder().member(memberEntity).build();

        // when
        SocialMemberProfile result = socialIdentityJpaMapper.toSocialMemberProfile(memberEntity,memberAuthEntity);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(),result.getAccountId().getValue());
        assertEquals(memberAuthEntity.getProvider(),result.getSocialCredentials().getProvider());
        assertEquals(memberAuthEntity.getProviderId(),result.getSocialCredentials().getProviderId());
        assertEquals(memberAuthEntity.getEmail(),result.getEmail().getValue());
        assertEquals(memberEntity.getNickname(),result.getNickname().getValue());
        assertEquals(memberEntity.getRole(),result.getRole());
    }



}