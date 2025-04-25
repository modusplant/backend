package kr.modusplant.modules.auth.normal.app.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import kr.modusplant.modules.auth.normal.app.service.NormalSignUpApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NormalSignUpController {

    private final NormalSignUpApplicationService normalSignUpApplicationService;
//    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "이용약관 정보 전달 API",
            description = "회원가입 시 이용약관 정보를 클라이언트에 전달합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: Fail to fetch data")
    })
    @GetMapping("/api/terms")
    public ResponseEntity<DataResponse<?>> sendTerms(){

            List<Term> terms = normalSignUpApplicationService.getAllTerms();
            List<Map<String, Object>> termMapList = normalSignUpApplicationService.createTermMapList(terms);

            DataResponse<List<Map<String, Object>>> successDataResponse =
                    DataResponse.of(200, "terms info successfully fetched", termMapList);

            return ResponseEntity.ok(successDataResponse);
    }

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