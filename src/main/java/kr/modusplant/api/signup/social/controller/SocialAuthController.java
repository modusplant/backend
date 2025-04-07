package kr.modusplant.api.signup.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.enums.AuthProvider;
import kr.modusplant.api.signup.social.model.external.GoogleUserInfo;
import kr.modusplant.api.signup.social.model.external.KakaoUserInfo;
import kr.modusplant.api.signup.social.model.request.SocialLoginRequest;
import kr.modusplant.api.signup.social.model.response.TokenResponse;
import kr.modusplant.api.signup.social.service.SocialAuthService;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name="Social Login API", description = "소셜 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    @Operation(summary = "카카오 소셜 로그인 API", description = "카카오 인가코드를 받아 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : Kakao Login"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid client input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or missing authentication credentials"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred on the Authorization server")
    })
    @PostMapping("/kakao/social-login")
    public ResponseEntity<DataResponse<?>> kakaoSocialLogin(@RequestBody SocialLoginRequest request) {
        /* 소셜 로그인 */
        // Kakao Token 발급
        String kakaoAccessToken = socialAuthService.getKakaoAccessToken(request.getCode());
        // Kakao 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = socialAuthService.getKakaoUserInfo(kakaoAccessToken);
        // 사용자 생성 및 조회
        SiteMember siteMember = socialAuthService.findOrCreateMember(AuthProvider.KAKAO, kakaoUserInfo.getKakaoId(),kakaoUserInfo.getKakaoEmail(),kakaoUserInfo.getKakaoNickname());

        /* JWT */
        // JWT 예시
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        TokenResponse token = new TokenResponse();
        token.setAccessToken(accessToken);

        DataResponse<TokenResponse> response = DataResponse.of(200,"OK: Succeeded", token);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구글 소셜 로그인 API", description = "구글 인가코드를 받아 로그인합니다.<br> 구글 인가코드를 url에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : Google Login"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid client input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or missing authentication credentials"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred on the Authorization server")
    })
    @PostMapping("/google/social-login")
    public ResponseEntity<DataResponse<?>> googleSocialLogin(@RequestBody SocialLoginRequest request) {

        /* 소셜 로그인 */
        // Google Token 발급
        String googleAccessToken = socialAuthService.getGoogleAccessToken(request.getCode());
        // Google 사용자 정보 가져오기
        GoogleUserInfo googleUserInfo = socialAuthService.getGoogleUserInfo(googleAccessToken);
        // 사용자 생성 및 조회
        SiteMember siteMember = socialAuthService.findOrCreateMember(AuthProvider.GOOGLE,googleUserInfo.getId(),googleUserInfo.getEmail(),googleUserInfo.getNickname());

        // JWT 예시
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        TokenResponse token = new TokenResponse();
        token.setAccessToken(accessToken);

        DataResponse<TokenResponse> response = DataResponse.of(200,"OK: Succeeded", token);

        return ResponseEntity.ok(response);
    }


}
