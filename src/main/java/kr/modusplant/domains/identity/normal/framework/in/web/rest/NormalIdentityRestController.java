package kr.modusplant.domains.identity.normal.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.identity.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.NormalLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "일반 계정 API", description = "일반 회원가입과 로그인을 다루는 API입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class NormalIdentityRestController {
    private final NormalIdentityController controller;

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    @Operation(
            summary = "일반 회원가입 API",
            description = "클라이언트가 보낸 정보를 통해 일반 회원가입합니다."
    )
    @PostMapping("/api/members/register")
    public ResponseEntity<DataResponse<Void>> registerNormalMember(@RequestBody @Valid NormalSignUpRequest registerRequest) {

        controller.registerNormalMember(registerRequest);
        DataResponse<Void> successDataResponse = DataResponse.ok();

        return ResponseEntity.ok(successDataResponse);

    }

    @Operation(
            summary = "일반 로그인 API",
            description = "토큰을 통해 일반 로그인 절차를 수행합니다."
    )
    @PostMapping("/api/auth/login-success")
    public ResponseEntity<DataResponse<Map<String, Object>>> respondToNormalLoginSuccess(
            @Parameter(schema = @Schema(
                    description = "접근 토큰")
            )
            @RequestAttribute("accessToken")
            @NotBlank(message = "접근 토큰이 비어 있습니다.")
            String accessToken,

            @Parameter(schema = @Schema(
                    description = "리프레시 토큰")
            )
            @RequestAttribute("refreshToken")
            @NotBlank(message = "리프레시 토큰이 비어 있습니다.")
            String refreshToken) {

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshDuration)
                .sameSite("Lax")
                .build();

        Map<String, Object> accessTokenData = Map.of("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .cacheControl(CacheControl.noStore())
                .body(DataResponse.ok(accessTokenData));
    }


    /**
     * Spring Security 필터인 EmailPasswordAuthenticationFilter 에 등록된
     * 일반 로그인 API 의 경로를 Swagger UI에 등록하기 위해 만들어진 더미 메서드입니다.
     * "절대로" 호출되거나, 사용될 일이 없습니다.
     * @param loginRequest 일반 회원가입에서 사용되는 이메일, 비밀번호로 구성되었습니다.
     */
    @Operation(
            summary = "일반 로그인 API",
            description = "이메일과 비밀번호로 일반 로그인 절차를 수행합니다."
    )
    @PostMapping("/api/auth/login")
    public void addLoginApiPathToSwaggerUi(@RequestBody @Valid NormalLoginRequest loginRequest) {

    }

}
