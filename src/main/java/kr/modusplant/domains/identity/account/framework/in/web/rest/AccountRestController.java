package kr.modusplant.domains.identity.account.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.identity.account.adapter.controller.AccountController;
import kr.modusplant.domains.identity.account.usecase.response.AccountAuthResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "계정 인증 API", description = "계정의 인증 정보를 다루는 API 입니다")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountController controller;

    @Operation(
            summary = "회원의 식별자로 회원의 이메일과 인증 제공자를 가져오는 API",
            description = "회원의 식별자에 맞는 계정의 인증 정보를 제공합니다."
    )
    @GetMapping("/v1/members/{id}/auth-info")
    public ResponseEntity<DataResponse<AccountAuthResponse>> getAuthInfo(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @PathVariable("id")
            @NotNull(message = "사용자의 식별자 값이 비어 있습니다")
            UUID memberActiveUuid
    ) {
        AccountAuthResponse response = controller.getAuthInfo(memberActiveUuid);
        return ResponseEntity.ok(DataResponse.ok(response));
    }

    /**
     * Spring Security의 보안 필터 체인에 설정된 로그아웃 URL을
     * Swagger UI에 등록하기 위해 만들어진 더미 메서드입니다.
     * <p>"절대로" 호출되거나, 사용될 일이 없습니다.<p/>
     * @param refreshToken 클라이언트가 쿠키로서 발송한 리프레시 토큰입니다.
     * @param accessToken 로그인한 사용자의 접근 토큰입니다.
     */
    @PostMapping("/auth/logout")
    public void processLogout(
            @Parameter(schema = @Schema(
                    description = "리프레시 토큰",
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3s"))
            @CookieValue("Cookie")
            String refreshToken,

            @Parameter(schema = @Schema(
                    description = "접근 토큰",
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyNDI2MjIsInJvbGUiOiJhZG1pbiJ9.4Adcj3UFYzPUVaVF43FmMab6RlaQD4u-Vd4GcSANrLo"))
            @RequestHeader("Authorization")
            String accessToken
    ) {

    }
}
