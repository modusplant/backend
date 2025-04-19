package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberRoleCrudService;
import kr.modusplant.domains.member.enums.AuthProvider;
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
class SiteMemberSocialAuthServiceTest {

    @Autowired
    private SiteMemberSocialAuthService siteMemberSocialAuthService;

    @Autowired
    private SiteMemberCrudService siteMemberCrudService;

    @Autowired
    private SiteMemberAuthCrudService siteMemberAuthCrudService;

    @Autowired
    private SiteMemberRoleCrudService siteMemberRoleCrudService;

    private final AuthProvider provider = AuthProvider.GOOGLE;
    private final String id = "968788539145693243421";
    private final String email = "test@example.com";
    private final String nickname = "test";

    @Test
    @DisplayName("이미 존재하는 사용자라면, 사용자 정보를 조회한다")
    void findOrCreateMemberWhenMemberExists() {
        // Given
        SiteMember existedMember = siteMemberCrudService.insert(
                SiteMember.builder()
                .nickname(nickname)
                .loggedInAt(LocalDateTime.now())
                .build());

        SiteMemberAuth existedMemberAuth = siteMemberAuthCrudService.insert(
                SiteMemberAuth.builder()
                        .activeMemberUuid(existedMember.getUuid())
                        .originalMemberUuid(existedMember.getUuid())
                        .email(email)
                        .provider(provider)
                        .providerId(id)
                        .build());

        SiteMemberRole existedMemberRole = siteMemberRoleCrudService.insert(
                SiteMemberRole.builder()
                        .uuid(existedMember.getUuid())
                        .role(Role.ROLE_USER)
                        .build());

        // when
        SiteMember result = siteMemberSocialAuthService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(existedMember.getUuid(), result.getUuid());
        assertEquals(existedMemberAuth.getActiveMemberUuid(), result.getUuid());
        assertEquals(existedMemberRole.getUuid(), result.getUuid());
    }

    @Test
    void findOrCreateMemberWhenMemberDoesNotExists() {
        // When
        SiteMember result = siteMemberSocialAuthService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(nickname, result.getNickname());
        assertNotNull(result.getUuid());

        SiteMemberAuth siteMemberAuth = siteMemberAuthCrudService.getByProviderAndProviderId(provider,id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("SiteMemberAuth not found"));
        assertEquals(email, siteMemberAuth.getEmail());
        assertEquals(result.getUuid(),siteMemberAuth.getActiveMemberUuid());

        SiteMemberRole siteMemberRole = siteMemberRoleCrudService.getByUuid(result.getUuid())
                .orElseThrow(() -> new AssertionError("SiteMemberRole not found"));
        assertEquals(Role.ROLE_USER, siteMemberRole.getRole());
        assertEquals(result.getUuid(), siteMemberRole.getUuid());
    }
}