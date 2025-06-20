package kr.modusplant.modules.auth.normal.signup.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import kr.modusplant.modules.auth.normal.signup.app.service.NormalSignUpApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "일반 회원가입 API", description = "일반 회원가입을 다루는 API입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NormalSignUpController {

    private final NormalSignUpApplicationService normalSignUpApplicationService;

    @Operation(
            summary = "일반 회원가입 API",
            description = "클라이언트가 전달한 정보로 일반 회원가입합니다."
    )
    @PostMapping("/api/members/register")
    public ResponseEntity<DataResponse<Void>> saveMember(@RequestBody NormalSignUpRequest memberData) {

        normalSignUpApplicationService.insertMember(memberData);
        DataResponse<Void> successDataResponse = DataResponse.ok();

        return ResponseEntity.ok(successDataResponse);

    }

}