package kr.modusplant.domains.identity.normal.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.identity.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.identity.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.identity.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.NormalLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

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
        return ResponseEntity.ok(DataResponse.ok());

    }

    @Operation(
            summary = "일반 회원의 이메일 수정 API",
            description = "사용자의 식별자, 현재 이메일, 새로운 이메일로 사용자의 이메일을 갱신합니다."
    )
    @PostMapping("/api/v1/members/{id}/modify/email")
    public ResponseEntity<DataResponse<Void>> modifyEmail(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @PathVariable("id")
            @NotNull(message = "사용자의 식별자 값이 비어 있습니다")
            UUID memberActiveUuid,

            @RequestBody @Valid
            EmailModificationRequest request
    ) {
        controller.modifyEmail(memberActiveUuid, request);

        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(
            summary = "일반 회원의 비밀번호 수정 API",
            description = "사용자의 식별자, 새로운 비밀번호로 사용자의 비밀번호를 갱신합니다."
    )
    @PostMapping("/api/v1/members/{id}/modify/password")
    public ResponseEntity<DataResponse<Void>> modifyPassword(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @PathVariable("id")
            @NotNull(message = "사용자의 식별자 값이 비어 있습니다")
            UUID memberActiveUuid,

            @RequestBody @Valid
            PasswordModificationRequest request
    ) {
        controller.modifyPassword(memberActiveUuid, request);

        return ResponseEntity.ok(DataResponse.ok());
    }


    /**
     * Spring Security 의 일반 로그인 필터 체인의
     * 성공 핸들러인 {@link kr.modusplant.infrastructure.security.handler.ForwardRequestLoginSuccessHandler} 가
     * 인증 완료 후 forward 하는 메서드입니다.
     * <p>클라이언트의 요청을 받는 도입부 역할을 하지 않으며, 따라서 Swagger UI에 표시하지 않습니다. <p/>
     *
     * @param accessToken 클라이언트에게 보내는 접근 토큰입니다.
     * @param refreshToken 클라이언트에게 보내는 갱신 토큰입니다.
     * @return 클라이언트에게 로그인에 대한 성공 응답을 반환합니다.
     */
    @Hidden
    @PostMapping("/api/auth/login-success")
    public ResponseEntity<DataResponse<Map<String, Object>>> respondToNormalLoginSuccess(
            @RequestAttribute("accessToken")
            @NotBlank(message = "접근 토큰이 비어 있습니다.")
            String accessToken,

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
     * Spring Security 필터인 {@link kr.modusplant.infrastructure.security.filter.EmailPasswordAuthenticationFilter} 에 등록된
     * 일반 로그인 API 의 경로를 Swagger UI에 등록하기 위해 만들어진 더미 메서드입니다.
     * <p>"절대로" 호출되거나, 사용될 일이 없습니다.<p/>
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
