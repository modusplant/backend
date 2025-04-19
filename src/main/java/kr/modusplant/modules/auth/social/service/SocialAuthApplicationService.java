package kr.modusplant.modules.auth.social.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.service.SiteMemberSocialAuthService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.modules.auth.social.dto.GoogleUserInfo;
import kr.modusplant.modules.auth.social.dto.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialAuthApplicationService {
    private final SocialAuthService socialAuthService;
    private final SiteMemberSocialAuthService siteMemberSocialAuthService;

    public SiteMember kakaoLogin(String code) {
        // Kakao Token 발급
        String kakaoAccessToken = socialAuthService.getKakaoAccessToken(code);
        // Kakao 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = socialAuthService.getKakaoUserInfo(kakaoAccessToken);
        // 사용자 생성 및 조회
        SiteMember siteMember = siteMemberSocialAuthService.findOrCreateMember(AuthProvider.KAKAO, kakaoUserInfo.getKakaoId(),kakaoUserInfo.getKakaoEmail(),kakaoUserInfo.getKakaoNickname());

        return siteMember;
    }

    public SiteMember googleLogin(String code) {
        /* 소셜 로그인 */
        // Google Token 발급
        String googleAccessToken = socialAuthService.getGoogleAccessToken(code);
        // Google 사용자 정보 가져오기
        GoogleUserInfo googleUserInfo = socialAuthService.getGoogleUserInfo(googleAccessToken);
        // 사용자 생성 및 조회
        SiteMember siteMember = siteMemberSocialAuthService.findOrCreateMember(AuthProvider.GOOGLE,googleUserInfo.getId(),googleUserInfo.getEmail(),googleUserInfo.getNickname());

        return siteMember;
    }
}
