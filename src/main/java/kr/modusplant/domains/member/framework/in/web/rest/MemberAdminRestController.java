package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.member.adapter.controller.MemberAdminController;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.modusplant.shared.constant.Regex.REGEX_ULID;

@Tag(name = "회원 API(관리자 전용)", description = "회원의 생성과 갱신(상태 제외), 회원 활동을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/admin/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
public class MemberAdminRestController {
    private final MemberAdminController memberAdminController;

    @Operation(
            summary = "건의 및 버그 제보 제거 API",
            description = "건의 사항 또는 버그 제보를 제거합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @DeleteMapping(value = "/report/proposal-or-bug/{reportUlid}")
    public ResponseEntity<DataResponse<Void>> removeProposalOrBugReport(
            @Parameter(
                    description = "삭제할 보고서의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "보고서 식별자가 비어 있습니다.")
            String reportUlid) {
        memberAdminController.removeProposalOrBug(new ProposalOrBugReportRemoveRecord(reportUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
