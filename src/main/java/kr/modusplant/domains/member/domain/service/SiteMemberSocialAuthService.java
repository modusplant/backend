package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberRoleCrudService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteMemberSocialAuthService {
    private final SiteMemberCrudService siteMemberCrudService;
    private final SiteMemberAuthCrudService siteMemberAuthCrudService;
    private final SiteMemberRoleCrudService siteMemberRoleCrudService;

    @Transactional
    public SiteMember findOrCreateMember(AuthProvider provider, String id, String email, String nickname) {
        // provider와 provider_id로 site_member_auth 사용자 조회
        Optional<SiteMemberAuth> existedMemberAuth = siteMemberAuthCrudService.getByProviderAndProviderId(provider,id);

        // 신규 멤버 저장 및 멤버 반환
        return existedMemberAuth.map(siteMemberAuth -> {
            return siteMemberCrudService.getByUuid(siteMemberAuth.getActiveMemberUuid()).get();
        }).orElseGet(() -> {
            SiteMember savedMember = createSiteMember(nickname);
            createSiteMemberAuth(savedMember.getUuid(),provider,id,email);
            createSiteMemberRole(savedMember.getUuid());
            return savedMember;
        });
    }

    private SiteMember createSiteMember(String nickname) {
        SiteMember siteMember = SiteMember.builder()
                .nickname(nickname)
                .loggedInAt(LocalDateTime.now())
                .build();
        return siteMemberCrudService.insert(siteMember);
    }

    private SiteMemberAuth createSiteMemberAuth(UUID memberUuid, AuthProvider provider, String id, String email) {
        SiteMemberAuth siteMemberAuth = SiteMemberAuth.builder()
                .activeMemberUuid(memberUuid)
                .originalMemberUuid(memberUuid)
                .email(email)
                .provider(provider)
                .providerId(id)
                .build();
        return siteMemberAuthCrudService.insert(siteMemberAuth);
    }

    private SiteMemberRole createSiteMemberRole(UUID memberUuid) {
        SiteMemberRole siteMemberRole = SiteMemberRole.builder()
                .uuid(memberUuid)
                .role(Role.ROLE_USER)
                .build();
        return siteMemberRoleCrudService.insert(siteMemberRole);
    }
}
