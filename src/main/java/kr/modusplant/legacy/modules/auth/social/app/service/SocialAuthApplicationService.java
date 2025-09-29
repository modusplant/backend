package kr.modusplant.legacy.modules.auth.social.app.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberAuthRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRoleRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberAuthDomainInfraMapper;
import kr.modusplant.legacy.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.legacy.modules.auth.social.app.dto.supers.SocialUserInfo;
import kr.modusplant.legacy.modules.auth.social.app.service.supers.SocialAuthClient;
import kr.modusplant.legacy.modules.auth.social.error.UnsupportedSocialProviderException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialAuthApplicationService {
    private final KakaoAuthClient kakaoAuthClient;
    private final GoogleAuthClient googleAuthClient;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRoleRepository memberRoleRepository;
    private final SiteMemberAuthDomainInfraMapper memberAuthEntityMapper;

    public JwtUserPayload handleSocialLogin(AuthProvider provider, String code) {
        // 소셜 토큰 발급
        String socialAccessToken = getClient(provider).getAccessToken(code);
        // 소셜 사용자 정보 가져오기
        SocialUserInfo user = getClient(provider).getUserInfo(socialAccessToken);
        // 사용자 생성 및 조회
        return findOrCreateMember(provider, user.getId(),user.getEmail(),user.getNickname());
    }

    private SocialAuthClient getClient(AuthProvider provider) {
        return switch (provider) {
            case KAKAO -> kakaoAuthClient;
            case GOOGLE -> googleAuthClient;
            default -> throw new UnsupportedSocialProviderException();
        };
    }

    @Transactional
    public JwtUserPayload findOrCreateMember(AuthProvider provider, String id, String email, String nickname) {
        // provider와 provider_id로 site_member_auth 사용자 조회
        Optional<SiteMemberAuth> existedMemberAuth = getMemberAuthByProviderAndProviderId(provider,id);

        // 신규 멤버 저장 및 멤버 반환
        return existedMemberAuth.map(siteMemberAuth -> {
            SiteMemberEntity memberEntity = getMemberEntityByUuid(siteMemberAuth.getActiveMemberUuid());
            memberEntity.updateLoggedInAt(LocalDateTime.now());
            memberRepository.save(memberEntity);
            Role role = getMemberRoleEntityByMember(memberEntity).getRole();
            return new JwtUserPayload(memberEntity.getUuid(), memberEntity.getNickname(), role);
        }).orElseGet(() -> {
            SiteMemberEntity memberEntity = createSiteMember(nickname);
            createSiteMemberAuth(memberEntity, provider, id, email);
            Role role = createSiteMemberRole(memberEntity).getRole();
            return new JwtUserPayload(memberEntity.getUuid(), memberEntity.getNickname(), role);
        });
    }

    private Optional<SiteMemberAuth> getMemberAuthByProviderAndProviderId(AuthProvider provider, String providerId) {
        Optional<SiteMemberAuthEntity> memberAuthEntityOrEmpty = memberAuthRepository.findByProviderAndProviderId(provider, providerId);
        return memberAuthEntityOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthEntityMapper.toSiteMemberAuth(memberAuthEntityOrEmpty.orElseThrow()));
    }

    private SiteMemberEntity getMemberEntityByUuid(UUID uuid) {
        return memberRepository.findByUuid(uuid)
                .orElseThrow((() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER)));
    }

    private SiteMemberRoleEntity getMemberRoleEntityByMember(SiteMemberEntity memberEntity) {
        return memberRoleRepository.findByMember(memberEntity)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_ROLE_NOT_FOUND, EntityName.SITE_MEMBER_ROLE));
    }

    private SiteMemberEntity createSiteMember(String nickname) {
        SiteMemberEntity memberEntity = SiteMemberEntity.builder()
                .nickname(nickname)
                .loggedInAt(LocalDateTime.now())
                .build();
        return memberRepository.save(memberEntity);
    }

    private void createSiteMemberAuth(SiteMemberEntity memberEntity, AuthProvider provider, String id, String email) {
        SiteMemberAuthEntity memberAuthEntity = SiteMemberAuthEntity.builder()
                .activeMember(memberEntity)
                .originalMember(memberEntity)
                .email(email)
                .provider(provider)
                .providerId(id)
                .build();

        memberAuthRepository.save(memberAuthEntity);
    }

    private SiteMemberRoleEntity createSiteMemberRole(SiteMemberEntity memberEntity) {
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder()
                .member(memberEntity)
                .role(Role.USER).build();

        return memberRoleRepository.save(memberRoleEntity);
    }

}
