package kr.modusplant.domains.member.framework.inbound.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.member.adapter.controller.MemberAdminController;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.response.CommentAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.PostAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportDashboardResponse;
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

import static kr.modusplant.shared.constant.Regex.REGEX_MATERIALIZED_PATH;
import static kr.modusplant.shared.constant.Regex.REGEX_ULID;

@Tag(name = "회원 API(관리자 전용)", description = "회원의 생성과 갱신(상태 제외), 회원 활동을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/admin/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class MemberAdminRestController {
    private final MemberAdminController memberAdminController;

    @Operation(
            summary = "건의 및 버그 제보 현황 조회 API (무한 스크롤)",
            description = "건의 사항 또는 버그 제보 현황을 조회합니다."
    )
    @GetMapping(value = "/report/proposal-or-bug")
    public ResponseEntity<DataResponse<ProposalOrBugReportDashboardResponse>> getProposalOrBugReport(
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
            @Range(min = 1, max = 50, message = "페이지 크기가 범위를 벗어났습니다. ")
            Integer size) {

        ProposalOrBugReportDashboardResponse response =
                memberAdminController.getProposalOrBug(new ProposalOrBugReportGetRecord(status, lastReportUlid, size));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
                .body(DataResponse.ok(response));
    }

    @Operation(
            summary = "건의 및 버그 제보 확인 API",
            description = "건의 사항 또는 버그 제보를 확인합니다."
    )
    @PostMapping(value = "/report/proposal-or-bug/{reportUlid}")
    public ResponseEntity<DataResponse<ProposalOrBugReportDashboardReadModel>> checkProposalOrBugReport(
            @Parameter(
                    description = "확인할 보고서의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "보고서 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String reportUlid) {
        ProposalOrBugReportDashboardReadModel readModel =
                memberAdminController.checkProposalOrBug(new ProposalOrBugReportCheckRecord(reportUlid));
        return ResponseEntity.ok().body(DataResponse.ok(readModel));
    }

    @Operation(
            summary = "건의 및 버그 제보 제거 API",
            description = "건의 사항 또는 버그 제보를 제거합니다."
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

    @Operation(
            summary = "게시글 신고 현황 조회 API (무한 스크롤)",
            description = "게시글 신고 현황을 조회합니다."
    )
    @GetMapping(value = "/report/abuse/post")
    public ResponseEntity<DataResponse<PostAbuseReportDashboardResponse>> getPostAbuseReport(
            @Parameter(
                    description = "필터링용 보고서 상태",
                    example = "UNCHECKED"
            )
            @RequestParam(name = "status", required = false)
            AbuseReportStatus status,

            @Parameter(
                    description = "마지막 게시글 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @RequestParam(name = "lastPostUlid", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String lastPostUlid,

            @Parameter(
                    description = "페이지 크기",
                    schema = @Schema(example = "10", minimum = "1", maximum = "50"))
            @RequestParam
            @Range(min = 1, max = 50, message = "페이지 크기가 범위를 벗어났습니다. ")
            Integer size) {

        PostAbuseReportDashboardResponse response =
                memberAdminController.getPostAbuseReport(new PostAbuseReportGetRecord(status, lastPostUlid, size));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
                .body(DataResponse.ok(response));
    }

    @Operation(
            summary = "게시글 신고 반려 API",
            description = "게시글 신고를 반려합니다."
    )
    @PostMapping(value = "/report/abuse/post/{postUlid}/dismiss")
    public ResponseEntity<DataResponse<PostAbuseReportDashboardReadModel>> dismissPostAbuseReport(
            @Parameter(
                    description = "반려할 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String postUlid) {
        PostAbuseReportDashboardReadModel readModel =
                memberAdminController.dismissPostAbuse(new PostAbuseReportDismissRecord(postUlid));
        return ResponseEntity.ok().body(DataResponse.ok(readModel));
    }

    @Operation(
            summary = "게시글 신고 수리 API",
            description = "게시글 신고를 수리합니다."
    )
    @PostMapping(value = "/report/abuse/post/{postUlid}/approve")
    public ResponseEntity<DataResponse<PostAbuseReportDashboardReadModel>> approvePostAbuseReport(
            @Parameter(
                    description = "수리할 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String postUlid) {
        PostAbuseReportDashboardReadModel readModel =
                memberAdminController.approvePostAbuse(new PostAbuseReportApproveRecord(postUlid));
        return ResponseEntity.ok().body(DataResponse.ok(readModel));
    }

    @Operation(
            summary = "댓글 신고 현황 조회 API (무한 스크롤)",
            description = "댓글 신고 현황을 조회합니다."
    )
    @GetMapping(value = "/report/abuse/comment")
    public ResponseEntity<DataResponse<CommentAbuseReportDashboardResponse>> getCommentAbuseReport(
            @Parameter(
                    description = "필터링용 보고서 상태",
                    example = "UNCHECKED"
            )
            @RequestParam(name = "status", required = false)
            AbuseReportStatus status,

            @Parameter(
                    description = "마지막 게시글 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @RequestParam(name = "lastPostUlid", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String lastPostUlid,

            @Parameter(
                    description = "마지막 댓글 경로",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @RequestParam(name = "lastPath", required = false)
            @Pattern(regexp = REGEX_MATERIALIZED_PATH, message = "유효하지 않은 댓글 경로 형식입니다. ")
            String lastPath,

            @Parameter(
                    description = "페이지 크기",
                    schema = @Schema(example = "10", minimum = "1", maximum = "50"))
            @RequestParam
            @Range(min = 1, max = 50, message = "페이지 크기가 범위를 벗어났습니다. ")
            Integer size) {

        CommentAbuseReportDashboardResponse response =
                memberAdminController.getCommentAbuseReport(
                        new CommentAbuseReportGetRecord(status, lastPostUlid, lastPath, size));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
                .body(DataResponse.ok(response));
    }

    @Operation(
            summary = "댓글 신고 반려 API",
            description = "댓글 신고를 반려합니다."
    )
    @PostMapping(value = "/report/abuse/post/{postUlid}/path/{path}/dismiss")
    public ResponseEntity<DataResponse<CommentAbuseReportDashboardReadModel>> dismissCommentAbuseReport(
            @Parameter(
                    description = "반려할 댓글이 속한 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String postUlid,

            @Parameter(
                    description = "반려할 댓글의 경로",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @PathVariable
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            @Pattern(regexp = REGEX_MATERIALIZED_PATH, message = "유효하지 않은 댓글 경로 형식입니다. ")
            String path) {
        CommentAbuseReportDashboardReadModel readModel =
                memberAdminController.dismissCommentAbuse(new CommentAbuseReportDismissRecord(postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok(readModel));
    }

    @Operation(
            summary = "댓글 신고 수리 API",
            description = "댓글 신고를 수리합니다."
    )
    @PostMapping(value = "/report/abuse/post/{postUlid}/path/{path}/approve")
    public ResponseEntity<DataResponse<CommentAbuseReportDashboardReadModel>> approveCommentAbuseReport(
            @Parameter(
                    description = "수리할 댓글이 속한 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String postUlid,

            @Parameter(
                    description = "수리할 댓글의 경로",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @PathVariable
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            @Pattern(regexp = REGEX_MATERIALIZED_PATH, message = "유효하지 않은 댓글 경로 형식입니다. ")
            String path) {
        CommentAbuseReportDashboardReadModel readModel =
                memberAdminController.approveCommentAbuse(new CommentAbuseReportApproveRecord(postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok(readModel));
    }
}
