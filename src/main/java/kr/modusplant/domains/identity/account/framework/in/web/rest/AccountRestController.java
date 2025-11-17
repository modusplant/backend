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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
