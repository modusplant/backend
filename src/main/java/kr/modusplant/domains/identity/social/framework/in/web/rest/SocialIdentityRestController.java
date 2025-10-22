package kr.modusplant.domains.identity.social.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.modusplant.domains.identity.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.usecase.request.SocialLoginRequest;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.http.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static kr.modusplant.shared.persistence.constant.TableColumnName.REFRESH_TOKEN;

@Tag(name = "소셜 로그인 API", description = "소셜 로그인을 다루는 API입니다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class SocialIdentityRestController {

    private final SocialIdentityController socialIdentityController;
    private final TokenService tokenService;

    @Operation(summary = "카카오 소셜 로그인 API", description = "카카오 인가 코드를 받아 로그인합니다.")
    @PostMapping("/kakao/social-login")
    public ResponseEntity<DataResponse<?>> kakaoSocialLogin(@RequestBody @Valid SocialLoginRequest request) {
        UserPayload member = socialIdentityController.handleSocialLogin(AuthProvider.KAKAO, request.getCode());

        TokenPair tokenPair = tokenService.issueToken(member.getMemberId().getValue(), member.getNickname().getNickname(), member.getRole());

        TokenResponse token = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = setRefreshTokenCookie(tokenPair.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    @Operation(summary = "구글 소셜 로그인 API", description = "구글 인가 코드를 받아 로그인합니다.<br>구글 인가 코드를 URL에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @PostMapping("/google/social-login")
    public ResponseEntity<DataResponse<?>> googleSocialLogin(@RequestBody @Valid SocialLoginRequest request) {
        UserPayload member = socialIdentityController.handleSocialLogin(AuthProvider.GOOGLE, request.getCode());

        TokenPair tokenPair = tokenService.issueToken(member.getMemberId().getValue(), member.getNickname().getNickname(), member.getRole());

        TokenResponse token = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = setRefreshTokenCookie(tokenPair.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    private String setRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        return refreshCookie.toString();
    }
}
