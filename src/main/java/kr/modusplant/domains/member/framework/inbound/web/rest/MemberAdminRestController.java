package kr.modusplant.domains.member.framework.inbound.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.member.adapter.controller.MemberAdminController;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportCheckRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportGetRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            summary = "건의 및 버그 제보 조회 API (무한 스크롤)",
            description = "건의 사항 또는 버그 제보를 조회합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @GetMapping(value = "/report/proposal-or-bug")
    public ResponseEntity<DataResponse<List<ProposalOrBugReportAdminPageReadModel>>> getProposalOrBugReport(
            @Parameter(
                    description = "필터링용 보고서 상태",
                    example = "UNCHECKED"
            )
            @RequestParam(name = "status", required = false)
            ProposalOrBugReportStatus status,

            @Parameter(
                    description = "마지막 보고서 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @RequestParam(name = "lastReportUlid", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String lastReportUlid,

            @Parameter(
                    description = "페이지 크기",
                    schema = @Schema(example = "10", minimum = "1", maximum = "50"))
            @RequestParam
            @Range(min = 1, max = 50)
            Integer size) {

        List<ProposalOrBugReportAdminPageReadModel> readModels =
                memberAdminController.getProposalOrBug(new ProposalOrBugReportGetRecord(status, lastReportUlid, size));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
                .body(DataResponse.ok(readModels));
    }

    @Operation(
            summary = "건의 및 버그 제보 확인 API",
            description = "건의 사항 또는 버그 제보를 확인합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping(value = "/report/proposal-or-bug/{reportUlid}")
    public ResponseEntity<DataResponse<ProposalOrBugReportAdminPageReadModel>> checkProposalOrBugReport(
            @Parameter(
                    description = "확인할 보고서의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "보고서 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String reportUlid) {
        ProposalOrBugReportAdminPageReadModel readModel =
                memberAdminController.checkProposalOrBug(new ProposalOrBugReportCheckRecord(reportUlid));
        return ResponseEntity.ok().body(DataResponse.ok(readModel));
    }

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
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String reportUlid) {
        memberAdminController.removeProposalOrBug(new ProposalOrBugReportRemoveRecord(reportUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
