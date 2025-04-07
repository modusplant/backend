package kr.modusplant.api.signup.social.service;

import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.api.crud.member.domain.service.SiteMemberAuthService;
import kr.modusplant.api.crud.member.domain.service.SiteMemberRoleService;
import kr.modusplant.api.crud.member.domain.service.SiteMemberService;
import kr.modusplant.api.crud.member.enums.AuthProvider;
import kr.modusplant.global.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class SocialAuthServiceIntegrationTest {

    @Autowired
    private SocialAuthService socialAuthService;

    @Autowired
    private SiteMemberService siteMemberService;

    @Autowired
    private SiteMemberAuthService siteMemberAuthService;

    @Autowired
    private SiteMemberRoleService siteMemberRoleService;

    @Test
    @DisplayName("이미 존재하는 사용자라면, 사용자 정보를 조회한다")
    void findOrCreateMemberWhenMemberExists() {
        // Given
        AuthProvider provider = AuthProvider.GOOGLE;
        String id = "968788539145693243421";
        String email = "test@example.com";
        String nickname = "test";
        SiteMember existedMember = siteMemberService.insert(
                SiteMember.builder()
                .nickname(nickname)
                .loggedInAt(LocalDateTime.now())
                .build());

        SiteMemberAuth existedMemberAuth = siteMemberAuthService.insert(
                SiteMemberAuth.builder()
                        .activeMemberUuid(existedMember.getUuid())
                        .originalMemberUuid(existedMember.getUuid())
                        .email(email)
                        .provider(provider)
                        .providerId(id)
                        .build());

        SiteMemberRole existedMemberRole = siteMemberRoleService.insert(
                SiteMemberRole.builder()
                        .uuid(existedMember.getUuid())
                        .role(Role.ROLE_USER)
                        .build());

        // when
        SiteMember result = socialAuthService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(existedMember.getUuid(), result.getUuid());
        assertEquals(existedMemberAuth.getActiveMemberUuid(), result.getUuid());
        assertEquals(existedMemberRole.getUuid(), result.getUuid());
    }

    @Test
    void findOrCreateMemberWhenMemberDoesNotExists() {
        // Given
        AuthProvider provider = AuthProvider.GOOGLE;
        String id = "968788539145693243421";
        String email = "test@example.com";
        String nickname = "test";

        // When
        SiteMember result = socialAuthService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(nickname, result.getNickname());
        assertNotNull(result.getUuid());

        SiteMemberAuth siteMemberAuth = siteMemberAuthService.getByProviderAndProviderId(provider,id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("SiteMemberAuth not found"));
        assertEquals(email, siteMemberAuth.getEmail());
        assertEquals(result.getUuid(),siteMemberAuth.getActiveMemberUuid());

        SiteMemberRole siteMemberRole = siteMemberRoleService.getByUuid(result.getUuid())
                .orElseThrow(() -> new AssertionError("SiteMemberRole not found"));
        assertEquals(Role.ROLE_USER, siteMemberRole.getRole());
        assertEquals(result.getUuid(), siteMemberRole.getUuid());

    }
}