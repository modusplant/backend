package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
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

    public SocialAccountPayload handleSocialLogin(AuthProvider provider, String code) {
        // 소셜 토큰 발급
        String socialAccessToken = clientFactory.getClient(provider).getAccessToken(code);
        // 소셜 사용자 정보 가져오기
        SocialUserInfo user = clientFactory.getClient(provider).getUserInfo(socialAccessToken);
        // 사용자 생성 및 조회
        return findOrCreateMember(socialIdentityMapper.toSocialUserProfile(provider,user));
    }

    @Transactional
    public SocialAccountPayload findOrCreateMember(SocialAccountProfile profile) {
        Optional<AccountId> existingMemberId = socialIdentityRepository.getMemberIdBySocialCredentials(profile.getSocialCredentials());

        if (existingMemberId.isPresent()) {
            return handleExistingMember(existingMemberId.get());
        } else {
            return handleNewMember(profile);
        }
    }

    private SocialAccountPayload handleExistingMember(AccountId accountId) {
        socialIdentityRepository.updateLoggedInAt(accountId);
        return socialIdentityRepository.getUserPayloadByMemberId(accountId);
    }

    private SocialAccountPayload handleNewMember(SocialAccountProfile profile) {
        return socialIdentityRepository.createSocialMember(profile, Role.USER);
    }

}
