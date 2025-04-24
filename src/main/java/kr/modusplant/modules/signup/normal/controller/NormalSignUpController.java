package kr.modusplant.modules.signup.normal.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberTermCrudService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.signup.normal.model.request.NormalSignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    private final TermCrudService termCrudService;
    private final SiteMemberTermCrudService siteMemberTermCrudService;
    private final SiteMemberAuthCrudService siteMemberAuthCrudService;
    private final SiteMemberCrudService siteMemberCrudService;
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

        try {

            List<Map<String, Object>> terms = termCrudService.getAll()
                    .stream()
                    .filter(term -> {
                        String termKey = term.getName();

                        return termKey.equals("이용약관") ||
                                termKey.equals("개인정보처리방침") ||
                                termKey.equals("광고성 정보 수신");
                    })
                    .map(this::createTermMap)
                    .toList();

            DataResponse<List<Map<String, Object>>> successDataResponse = DataResponse.of(200, "terms info successfully fetched", terms);

            return ResponseEntity.ok(successDataResponse);

        } catch (Exception e) {
            log.info("Exception occurs in sendTerms. Content: ", e);

            String exceptionMessage = "error while getting data";
            if(e instanceof EmptyResultDataAccessException) {
                exceptionMessage = "error related to SQL";
            } else if (e instanceof IllegalStateException) {
                exceptionMessage = "invalid database state";
            }

            DataResponse<Void> errorDataResponse = DataResponse.of(500, exceptionMessage);

            return ResponseEntity.ok(errorDataResponse);
        }
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
        log.info("SignUpData: {}", memberData);

        try {

            if(memberData.pw().equals(memberData.pw_check())) {
                insertMember(memberData);
                DataResponse<Void> successDataResponse = DataResponse.of(200, "sign up successfully");

                return ResponseEntity.ok(successDataResponse);

            } else {
                DataResponse<Void> errorDataResponse = DataResponse.of(400, "pw and pw_check not equivalent");

                return ResponseEntity.ok(errorDataResponse);
            }

        } catch (Exception e) {
            String exceptionMessage = "";

            switch (e) {
                case JsonParseException jsonParseException -> exceptionMessage = "parsing json string failed";
                case UnrecognizedPropertyException unrecognizedPropertyException ->
                        exceptionMessage = "json property not found";
                case InvalidDataAccessResourceUsageException invalidDataAccessResourceUsageException ->
                        exceptionMessage = "invalid table or column name";
                case DataIntegrityViolationException dataIntegrityViolationException ->
                        exceptionMessage = "data constraints validated";
                default -> exceptionMessage = "error while saving member";
            }

            DataResponse<Void> errorDataResponse = DataResponse.of(500, exceptionMessage);

            return ResponseEntity.ok(errorDataResponse);
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

    @Transactional
    private void insertMember(NormalSignUpRequest memberData) {
        SiteMember siteMember = SiteMember.builder()
                .nickname(memberData.nickname())
                .build();
        SiteMember savedMember = siteMemberCrudService.insert(siteMember);

        SiteMemberAuth siteMemberAuth = SiteMemberAuth.builder()
                .uuid(savedMember.getUuid())
                .activeMemberUuid(savedMember.getUuid())
                .originalMemberUuid(savedMember.getUuid())
                .email(memberData.email())
//                .pw(passwordEncoder.encode(requestNode.get("pw")))
                .pw(memberData.pw())
                .provider(AuthProvider.BASIC)
                .build();
        siteMemberAuthCrudService.insert(siteMemberAuth);

        SiteMemberTerm siteMemberTerm = SiteMemberTerm.builder()
                .uuid(savedMember.getUuid())
                .agreedTermsOfUseVersion(memberData.agreedTermsOfUseVerion())
                .agreedPrivacyPolicyVersion(memberData.agreedPrivacyPolicyVerion())
                .agreedAdInfoReceivingVersion(memberData.agreedAdInfoRecevingVerion())
                .build();
        siteMemberTermCrudService.insert(siteMemberTerm);
    }
}