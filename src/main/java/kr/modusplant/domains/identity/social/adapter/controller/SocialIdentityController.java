package kr.modusplant.domains.identity.social.adapter.controller;

import kr.modusplant.domains.identity.social.domain.vo.MemberId;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.identity.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.identity.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.identity.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SocialIdentityController {

    private final SocialAuthClientFactory clientFactory;
    private final SocialIdentityRepository socialIdentityRepository;
    private final SocialIdentityMapper socialIdentityMapper;

    public UserPayload handleSocialLogin(AuthProvider provider, String code) {
        // 소셜 토큰 발급
        String socialAccessToken = clientFactory.getClient(provider).getAccessToken(code);
        // 소셜 사용자 정보 가져오기
        SocialUserInfo user = clientFactory.getClient(provider).getUserInfo(socialAccessToken);
        // 사용자 생성 및 조회
        return findOrCreateMember(socialIdentityMapper.toSocialUserProfile(provider,user));
    }

    @Transactional
    public UserPayload findOrCreateMember(SocialUserProfile profile) {
        Optional<MemberId> existingMemberId = socialIdentityRepository.getMemberIdBySocialCredentials(profile.getSocialCredentials());

        if (existingMemberId.isPresent()) {
            return handleExistingMember(existingMemberId.get());
        } else {
            return handleNewMember(profile);
        }
    }

    private UserPayload handleExistingMember(MemberId memberId) {
        socialIdentityRepository.updateLoggedInAt(memberId);
        return socialIdentityRepository.getUserPayloadByMemberId(memberId);
    }

    private UserPayload handleNewMember(SocialUserProfile profile) {
        return socialIdentityRepository.createSocialMember(profile, Role.USER);
    }

}
