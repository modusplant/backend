package kr.modusplant.modules.auth.normal.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import kr.modusplant.modules.auth.normal.app.service.NormalSignUpApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NormalSignUpController {

    private final NormalSignUpApplicationService normalSignUpApplicationService;

    @Operation(
            summary = "일반 회원가입 API",
            description = "클라이언트가 전달한 정보로 일반 회원가입 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: fail to sign up")
    })
    @PostMapping("/api/members/register")
    public ResponseEntity<DataResponse<Void>> saveMember(@RequestBody NormalSignUpRequest memberData) {

        normalSignUpApplicationService.insertMember(memberData);
        DataResponse<Void> successDataResponse = DataResponse.of(200, "sign up successfully");

        return ResponseEntity.ok(successDataResponse);

    }

}