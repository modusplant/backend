package kr.modusplant.signUp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.persistence.service.SiteMemberAuthServiceImpl;
import kr.modusplant.global.persistence.service.SiteMemberServiceImpl;
import kr.modusplant.global.persistence.service.SiteMemberTermServiceImpl;
import kr.modusplant.global.persistence.service.TermServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SignUpController {

    private final TermServiceImpl termService;
    private final SiteMemberTermServiceImpl siteMemberTermService;
    private final SiteMemberAuthServiceImpl siteMemberAuthService;
    private final SiteMemberServiceImpl siteMemberService;
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
    public ResponseEntity<Map<String, Object>> sendTerms(){

        try {
            List<Map<String, Object>> responseDataValue = termService.getAll()
                    .stream()
                    .map(this::createTermMap)
                    .toList();

            Map<String, Object> response = Map.of(
                    "metadata", Map.of(
                            "status", 200,
                            "message", "terms info successfully fetched"
                    ),
                    "data", responseDataValue
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.info("Exception occurs in sendTerms. Content: ", e);

            String exceptionMessage = "error while getting term data";
            if(e instanceof EmptyResultDataAccessException) {
                exceptionMessage = "error related to SQL";
            } else if (e instanceof IllegalStateException) {
                exceptionMessage = "invalid database state";
            }

            Map<String, Object> errorResponse = Map.of(
                    "metadata", Map.of(
                            "status", 500,
                            "message", exceptionMessage
                    ),
                    "data", Collections.emptyMap()
            );

            return ResponseEntity.ok(errorResponse);
        }
    }

//    @Operation(
//            summary = "일반 회원가입 API",
//            description = "클라이언트가 전달한 정보로 일반 회원가입을 합니다."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
//            @ApiResponse(responseCode = "500", description = "Internal Server Error: fail to sign up")
//    })
//    public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody String email) {
//        //
//    }

    @Operation(
            summary = "일반 회원가입 API",
            description = "클라이언트가 전달한 정보로 일반 회원가입 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: fail to sign up")
    })
    @PostMapping("/api/members/register")
    public ResponseEntity<Map<String, Object>> saveMember(@RequestBody String memberAuthTermData) {
        log.info("SignUpData: {}", memberAuthTermData);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> requestMap = mapper.readValue(memberAuthTermData, Map.class);

            if(requestMap.get("pw").equals(requestMap.get("pw_check"))) {
                insertMember(requestMap);
                Map<String, Object> successResponse = Map.of(
                        "status", 200,
                        "message", "OK: sign up successfully"
                );

                return ResponseEntity.ok(successResponse);

            } else {
                Map<String, Object> errorResponse = Map.of(
                        "status", 400,
                        "message", "pw and pw_ckeck not equivalent"
                );

                return ResponseEntity.ok(errorResponse);
            }

        } catch (Exception e) {
            String exceptionMessage = "error while saving member";

            switch (e) {
                case JsonParseException jsonParseException -> exceptionMessage = "parsing json string failed";
                case UnrecognizedPropertyException unrecognizedPropertyException ->
                        exceptionMessage = "json property not found";
                case InvalidDataAccessResourceUsageException invalidDataAccessResourceUsageException ->
                        exceptionMessage = "invalid table or column name";
                case DataIntegrityViolationException dataIntegrityViolationException ->
                        exceptionMessage = "data constraints validated";
                default -> {
                }
            }

            Map<String, Object> errorResponse = Map.of(
                    "status", 500,
                    "message", exceptionMessage
            );

            return ResponseEntity.ok(errorResponse);
        }

    }

    private Map<String, Object> createTermMap(Term term) {
        String mapKey = switch (term.getName()) {
            case ("개인정보처리방침") -> "privacyPolicy";
            case ("이용약관") -> "termsOfUse";
            case ("광고성 정보 수신") -> "adInfoReceiving";
            default -> "unKnownTerm";
        };

        return Map.of(mapKey, term);
    }

    private void insertMember(Map<String, String> requestMap) {
        SiteMemberTerm siteMemberTerm = SiteMemberTerm.builder()
                .agreedTermsOfUseVersion(requestMap.get("agreedTermsOfUseVerion"))
                .agreedPrivacyPolicyVersion(requestMap.get("agreedPrivacyPolicyVerion"))
                .agreedAdInfoReceivingVersion(requestMap.get("agreedAdInfoRecevingVerion"))
                .build();

        SiteMemberAuth siteMemberAuth = SiteMemberAuth.builder()
                .email(requestMap.get("email"))
//                .pw(passwordEncoder.encode(requestNode.get("pw")))
                .pw(requestMap.get("pw"))
                .build();

        SiteMember siteMember = SiteMember.builder()
                .nickname(requestMap.get("nickname"))
                .build();

        siteMemberService.insert(siteMember);
        siteMemberAuthService.insert(siteMemberAuth);
        siteMemberTermService.insert(siteMemberTerm);
    }
}