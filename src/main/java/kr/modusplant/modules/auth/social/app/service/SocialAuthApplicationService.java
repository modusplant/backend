package kr.modusplant.modules.auth.social.app.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.modules.auth.social.app.dto.supers.SocialUserInfo;
import kr.modusplant.modules.auth.social.app.service.supers.SocialAuthClient;
import kr.modusplant.modules.auth.social.mapper.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SiteMemberEntityMapper memberEntityMapper = new SiteMemberEntityMapperImpl();
    private final SiteMemberAuthEntityMapper memberAuthEntityMapper = new SiteMemberAuthEntityMapperImpl();
    private final SiteMemberRoleEntityMapper memberRoleEntityMapper = new SiteMemberRoleEntityMapperImpl();

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
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인 방식입니다: " + provider);
        };
    }

    @Transactional
    public JwtUserPayload findOrCreateMember(AuthProvider provider, String id, String email, String nickname) {
        // provider와 provider_id로 site_member_auth 사용자 조회
        Optional<SiteMemberAuth> existedMemberAuth = getMemberAuthByProviderAndProviderId(provider,id);

        // 신규 멤버 저장 및 멤버 반환
        return existedMemberAuth.map(siteMemberAuth -> {
            SiteMemberEntity memberEntity = getMemberEntityByUuid(siteMemberAuth.getActiveMemberUuid());
            Role role = getMemberRoleEntityByMember(memberEntity).getRole();
            return new JwtUserPayload(memberEntity.getUuid(), memberEntity.getNickname(), role);
        }).orElseGet(() -> {
            SiteMemberEntity memberEntity = createSiteMember(nickname);
            createSiteMemberAuth(memberEntity.getUuid(),provider,id,email);
            Role role = createSiteMemberRole(memberEntity.getUuid()).getRole();
            return new JwtUserPayload(memberEntity.getUuid(), memberEntity.getNickname(), role);
        });
    }

    private Optional<SiteMemberAuth> getMemberAuthByProviderAndProviderId(AuthProvider provider, String providerId) {
        Optional<SiteMemberAuthEntity> memberAuthEntityOrEmpty = memberAuthRepository.findByProviderAndProviderId(provider, providerId);
        return memberAuthEntityOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthEntityMapper.toSiteMemberAuth(memberAuthEntityOrEmpty.orElseThrow()));
    }

    private SiteMemberEntity getMemberEntityByUuid(UUID uuid) {
        return memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("SiteMemberEntity를 찾지 못했습니다."));
    }

    private SiteMemberRoleEntity getMemberRoleEntityByMember(SiteMemberEntity memberEntity) {
        return memberRoleRepository.findByMember(memberEntity)
                .orElseThrow(() -> new EntityNotFoundException("SiteMemberRoleEntity를 찾지 못했습니다."));
    }

    private SiteMemberEntity createSiteMember(String nickname) {
        return memberRepository.save(memberEntityMapper.toSiteMemberEntity(nickname));
    }

    private SiteMemberAuthEntity createSiteMemberAuth(UUID memberUuid, AuthProvider provider, String id, String email) {
        return memberAuthRepository.save(memberAuthEntityMapper.toSiteMemberAuthEntity(memberUuid,provider,id,email,memberRepository));
    }

    private SiteMemberRoleEntity createSiteMemberRole(UUID memberUuid) {
        return memberRoleRepository.save(memberRoleEntityMapper.toSiteMemberRoleEntity(memberUuid, memberRepository));
    }

}
