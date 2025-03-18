package kr.modusplant.api.crud.controller;

import kr.modusplant.api.crud.model.external.GoogleUserInfo;
import kr.modusplant.api.crud.model.external.KakaoUserInfo;
import kr.modusplant.api.crud.model.external.OAuthToken;
import kr.modusplant.api.crud.model.request.SocialLoginRequest;
import kr.modusplant.api.crud.model.response.SingleDataResponse;
import kr.modusplant.api.crud.model.response.TokenResponse;
import kr.modusplant.api.crud.service.SocialAuthService;
import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    @PostMapping("/kakao/social-login")
    public ResponseEntity<SingleDataResponse<?>> kakaoSocialLogin(@RequestBody SocialLoginRequest request) {
        System.out.println("api");
        System.out.println(request.getCode());

        /* 소셜 로그인 */
        // Kakao Token 발급
        OAuthToken kakaoToken = socialAuthService.getKakaoToken(request.getCode());
        // Kakao 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = socialAuthService.getKakaoUserInfo(kakaoToken.getAccess_token());
        // 사용자 생성 및 조회
        SiteMember siteMember = socialAuthService.findOrCreateMember(AuthProvider.KAKAO, String.valueOf(kakaoUserInfo.getId()),kakaoUserInfo.getKakaoAccount().getEmail(),kakaoUserInfo.getKakaoAccount().getProfile().getNickname());

        /* JWT */
        // JWT 예시
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        TokenResponse token = new TokenResponse();
        token.setAccessToken(accessToken);

        SingleDataResponse<TokenResponse> response = SingleDataResponse.success(200,"OK: Succeeded", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/google/social-login")
    public ResponseEntity<SingleDataResponse<?>> googleSocialLogin(@RequestBody SocialLoginRequest request) {

        /* 소셜 로그인 */
        // Google Token 발급
        OAuthToken googleToken = socialAuthService.getGoogleToken(request.getCode());
        // Google 사용자 정보 가져오기
        GoogleUserInfo googleUserInfo = socialAuthService.getGoogleUserInfo(googleToken.getAccess_token());
        // 사용자 생성 및 조회
        SiteMember siteMember = socialAuthService.findOrCreateMember(AuthProvider.GOOGLE,googleUserInfo.getId(),googleUserInfo.getEmail(),googleUserInfo.getNickname());

        // JWT 예시
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        TokenResponse token = new TokenResponse();
        token.setAccessToken(accessToken);

        SingleDataResponse<TokenResponse> response = SingleDataResponse.success(200,"OK: Succeeded", token);

        return ResponseEntity.ok(response);
    }


}
