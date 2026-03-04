package kr.modusplant.domains.account.normal.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.account.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.account.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.account.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.NormalLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "일반 계정 API", description = "일반 회원가입과 로그인을 다루는 API입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class NormalIdentityRestController {
    private final NormalIdentityController controller;

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
