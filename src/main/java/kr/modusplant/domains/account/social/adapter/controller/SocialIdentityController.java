package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.exception.AlreadyRegisteredWithOtherProviderException;
import kr.modusplant.domains.account.social.domain.exception.InvalidValueException;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.*;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.domains.account.social.usecase.record.TempTokenInfo;
import kr.modusplant.domains.account.social.usecase.request.SocialSignUpRequest;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.domains.account.social.usecase.response.NeedLinkResult;
import kr.modusplant.domains.account.social.usecase.response.NeedSignupResult;
import kr.modusplant.domains.account.social.usecase.response.SocialLoginResult;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SocialIdentityController {

    private final SocialAuthClientFactory clientFactory;
    private final SocialIdentityRepository socialIdentityRepository;
    private final SocialIdentityMapper socialIdentityMapper;

    public String issueSocialAccessToken(SocialProvider provider, String code) {
        return clientFactory.getClient(provider).getAccessToken(code);
    }

    public SocialLoginResult handleSocialLogin(SocialProvider provider, String socialAccessToken) {
        // 소셜 사용자 정보 가져오기
        SocialUserInfo user = clientFactory.getClient(provider).getUserInfo(socialAccessToken);
        // 사용자 생성 및 조회
        return classifyMember(socialIdentityMapper.toSocialProfile(provider,user));
    }

    @Transactional
    public SocialLoginResult classifyMember(SocialProfile profile) {
        Optional<SocialMemberProfile> existingMemberOptional = socialIdentityRepository.getSocialMemberProfileByEmail(Email.create(profile.getEmail().getValue()));
        // 회원가입 연결 : NEED_SIGNUP
        if (existingMemberOptional.isEmpty()) {
            return new NeedSignupResult(profile.getEmail().getValue(),profile.getSocialNickname(), profile.getProviderId(), profile.getSocialProvider());
        }
        SocialMemberProfile existingMember = existingMemberOptional.get();
        // 연동 연결 : NEED_LINK
        if (existingMember.getSocialCredentials().isPureBasic()) {
            return new NeedLinkResult(existingMember.getEmail().getValue(),existingMember.getNickname().getValue(),profile.getProviderId(),profile.getSocialProvider());
        }
        // 로그인
        // 다른 소셜로 가입된 계정일 경우 예외 처리
        if (!matches(profile.getSocialProvider(),existingMember.getSocialCredentials())) {
            switch (profile.getSocialProvider()) {
                case KAKAO -> throw new AlreadyRegisteredWithOtherProviderException(SocialIdentityErrorCode.ALREADY_REGISTERED_WITH_GOOGLE);
                case GOOGLE ->  throw new AlreadyRegisteredWithOtherProviderException(SocialIdentityErrorCode.ALREADY_REGISTERED_WITH_KAKAO);
            }
        }
        // 로그인 완료 : LOGIN
        if (existingMember.getSocialCredentials().getProviderId().equals(profile.getProviderId())) {
            return handleExistingSocialMember(existingMember.getAccountId());
        }
        throw new InvalidValueException(SocialIdentityErrorCode.INVALID_PROVIDER_ID);
    }

    private LoginResult handleExistingSocialMember(AccountId accountId) {
        return socialIdentityMapper.toLoginResult(socialIdentityRepository.updateLoggedInAtAndGetProfile(accountId));
    }

    private boolean matches(SocialProvider socialProvider, SocialCredentials socialCredentials) {
        return switch (socialProvider) {
            case KAKAO ->  socialCredentials.isKakao();
            case GOOGLE -> socialCredentials.isGoogle();
        };
    }

    // 회원가입 완료
    @Transactional
    public LoginResult createNewMember(SocialSignUpRequest signUpRequest, TempTokenInfo tempTokenInfo) {
        Optional<SocialMemberProfile> member = socialIdentityRepository.getSocialMemberProfileByEmail(Email.create(tempTokenInfo.email()));
        if (member.isPresent()) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.ALREADY_SIGNED_UP);
        }
        SocialMemberProfile memberProfile = SocialMemberProfile.createNewMember(
                SocialCredentials.create(socialIdentityMapper.toSocialAuthProvider(tempTokenInfo.socialProvider()), tempTokenInfo.providerId()),
                Email.create(tempTokenInfo.email()),
                Nickname.create(signUpRequest.nickname()),
                Role.USER
        );
        AgreedTerms agreedTerms = AgreedTerms.create(AgreedTermVersion.create(signUpRequest.agreedTermsOfUseVersion()),AgreedTermVersion.create(signUpRequest.agreedPrivacyPolicyVersion()),AgreedTermVersion.create(signUpRequest.agreedCommunityPolicyVersion()));
        return socialIdentityMapper.toLoginResult(socialIdentityRepository.saveSocialMember(memberProfile, signUpRequest.introduction(), agreedTerms));
    }

    // 기존 일반 계정과 연동
    @Transactional
    public LoginResult linkBasicSocialMember(TempTokenInfo tempTokenInfo) {
        AuthProvider linkedAuthProvider = socialIdentityMapper.toLinkedAuthProvider(tempTokenInfo.socialProvider());
        return socialIdentityMapper.toLoginResult(
                socialIdentityRepository.updateSocialLinkedMember(SocialCredentials.create(linkedAuthProvider, tempTokenInfo.providerId()), Email.create(tempTokenInfo.email()))
        );
    }

    public void unlinkSocialAccount(SocialProvider socialProvider, String socialAccessToken) {
        clientFactory.getClient(socialProvider).revokeAccess(socialAccessToken);
    }

}
