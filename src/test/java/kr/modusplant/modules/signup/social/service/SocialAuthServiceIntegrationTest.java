package kr.modusplant.modules.signup.social.service;

import kr.modusplant.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberAuthApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.domains.member.common.util.app.http.request.SiteMemberAuthRequestTestUtils;
import kr.modusplant.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.domains.member.common.util.app.http.request.SiteMemberRoleRequestTestUtils;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.global.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class SocialAuthServiceIntegrationTest implements SiteMemberRequestTestUtils, SiteMemberAuthRequestTestUtils, SiteMemberRoleRequestTestUtils {

    @Autowired
    private SocialAuthService socialAuthService;

    @Autowired
    private SiteMemberApplicationService siteMemberApplicationService;

    @Autowired
    private SiteMemberAuthApplicationService siteMemberAuthApplicationService;

    @Autowired
    private SiteMemberRoleApplicationService siteMemberRoleApplicationService;

    @Test
    @DisplayName("이미 존재하는 사용자라면, 사용자 정보를 조회한다")
    void findOrCreateMemberWhenMemberExists() {
        // Given
        SiteMemberResponse existedMember = siteMemberApplicationService.insert(memberGoogleUserInsertRequest);
        UUID uuid = existedMember.uuid();

        SiteMemberAuthResponse existedMemberAuth = siteMemberAuthApplicationService.insert(new SiteMemberAuthInsertRequest(uuid, memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getPw(), memberAuthGoogleUser.getProvider(), memberAuthGoogleUser.getProviderId()));

        SiteMemberRoleResponse existedMemberRole = siteMemberRoleApplicationService.insert(new SiteMemberRoleInsertRequest(uuid, memberRoleUser.getRole()));

        // when
        SiteMemberResponse result = socialAuthService.findOrCreateMember(memberAuthGoogleUser.getProvider(), memberAuthGoogleUser.getProviderId(), memberAuthGoogleUser.getEmail(), memberGoogleUser.getNickname());

        // Then
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(existedMemberAuth.activeMemberUuid(), result.uuid());
        assertEquals(existedMemberRole.uuid(), result.uuid());
    }

    @Test
    void findOrCreateMemberWhenMemberDoesNotExists() {
        // Given
        AuthProvider provider = memberAuthGoogleUser.getProvider();
        String id = memberAuthGoogleUser.getProviderId();
        String email = memberAuthGoogleUser.getEmail();
        String nickname = memberGoogleUser.getNickname();

        // When
        SiteMemberResponse result = socialAuthService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(nickname, result.nickname());
        assertNotNull(result.uuid());

        SiteMemberAuthResponse siteMemberAuth = siteMemberAuthApplicationService.getByProviderAndProviderId(provider,id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("SiteMemberAuth not found"));
        assertEquals(email, siteMemberAuth.email());
        assertEquals(result.uuid(),siteMemberAuth.activeMemberUuid());

        SiteMemberRoleResponse siteMemberRole = siteMemberRoleApplicationService.getByUuid(result.uuid())
                .orElseThrow(() -> new AssertionError("SiteMemberRole not found"));
        assertEquals(Role.ROLE_USER, siteMemberRole.role());
        assertEquals(result.uuid(), siteMemberRole.uuid());

    }
}