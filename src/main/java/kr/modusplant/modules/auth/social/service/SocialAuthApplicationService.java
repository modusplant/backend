package kr.modusplant.modules.auth.social.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.service.SiteMemberSocialAuthService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.modules.auth.social.dto.supers.SocialUserInfo;
import kr.modusplant.modules.auth.social.service.supers.SocialAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialAuthApplicationService {
    private final SiteMemberSocialAuthService siteMemberSocialAuthService;
    private final KakaoAuthClient kakaoAuthClient;
    private final GoogleAuthClient googleAuthClient;

    public SiteMember handleSocialLogin(AuthProvider provider, String code) {
        // 소셜 토큰 발급
        String socialAccessToken = getClient(provider).getAccessToken(code);
        // 소셜 사용자 정보 가져오기
        SocialUserInfo user = getClient(provider).getUserInfo(socialAccessToken);
        // 사용자 생성 및 조회
        return siteMemberSocialAuthService.findOrCreateMember(provider, user.getId(),user.getEmail(),user.getNickname());
    }

    private SocialAuthClient getClient(AuthProvider provider) {
        return switch (provider) {
            case KAKAO -> kakaoAuthClient;
            case GOOGLE -> googleAuthClient;
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인 방식입니다: " + provider);
        };
    }
}
