package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.in.web.cache.record.MemberCacheValidationResult;
import kr.modusplant.domains.member.framework.in.web.cache.service.MemberCacheValidationService;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.request.MemberWithdrawRequest;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static kr.modusplant.infrastructure.jwt.util.TokenUtils.getTokenFromAuthorizationHeader;
import static kr.modusplant.shared.constant.Regex.*;

@Tag(name = "회원 API", description = "회원의 생성과 갱신(상태 제외), 회원이 할 수 있는 단일한 기능을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MemberRestController {
    private final MemberController memberController;
    private final MemberCacheValidationService memberCacheValidationService;

    @Operation(summary = "회원 닉네임 중복 확인 API", description = "이미 등록된 닉네임이 있는지 조회합니다.")
    @GetMapping(value = "/members/check/nickname/{nickname}")
    public ResponseEntity<DataResponse<Map<String, Boolean>>> checkExistedMemberNickname(
            @Parameter(
                    description = "중복을 확인하려는 회원의 닉네임",
                    example = "IsThisNickname",
                    schema = @Schema(type = "string", pattern = REGEX_NICKNAME)
            )
            @PathVariable(required = false)
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME,
                    message = "닉네임은 2 ~ 10자까지 가능하며, 특수문자는 사용할 수 없습니다.")
            String nickname) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
                .body(
                        DataResponse.ok(
                                Map.of(
                                        "isNicknameExisted",
                                        memberController.checkExistedNickname(new MemberNicknameCheckRecord(nickname))
                                )
                        )
                );
    }

    @Operation(
            summary = "회원 프로필 조회 API",
            description = "기존 회원 프로필을 조회합니다. ",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @GetMapping(value = "/members/profile")
    public ResponseEntity<DataResponse<MemberProfileResponse>> getMemberProfile(
            @Parameter(hidden = true)
            @RequestHeader(name = HttpHeaders.IF_NONE_MATCH, required = false)
            String ifNoneMatch,

            @Parameter(hidden = true)
            @RequestHeader(name = HttpHeaders.IF_MODIFIED_SINCE, required = false)
            String ifModifiedSince,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) throws IOException {
        MemberCacheValidationResult cacheValidationResult =
                memberCacheValidationService.getMemberCacheValidationResult(ifNoneMatch, ifModifiedSince, memberId);
        if (cacheValidationResult.isCacheUsable()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate())
                    .eTag(String.format("W/\"%s\"", cacheValidationResult.entityTag()))
                    .lastModified(
                            ZonedDateTime.of(
                                    cacheValidationResult.lastModifiedDateTime(),
                                    ZoneId.of("Asia/Seoul")))
                    .build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate())
                    .eTag(String.format("W/\"%s\"", cacheValidationResult.entityTag()))
                    .lastModified(
                            ZonedDateTime.of(
                                    cacheValidationResult.lastModifiedDateTime(),
                                    ZoneId.of("Asia/Seoul")))
                    .body(DataResponse.ok(memberController.getProfile(new MemberProfileGetRecord(memberId))));
        }
    }

    @Operation(
            summary = "회원 프로필 덮어쓰기 API",
            description = "기존 회원 프로필을 덮어씁니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PutMapping(value = "/members/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<MemberProfileResponse>> overrideMemberProfile(
            @Parameter(
                    description = "갱신할 회원의 프로필 이미지",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart(name = "image", required = false)
            MultipartFile image,

            @Parameter(description = "갱신할 회원의 프로필 소개", example = "프로필 소개")
            @RequestPart(name = "introduction", required = false)
            String introduction,

            @Parameter(
                    description = "갱신할 회원의 닉네임",
                    example = "NewPlayer",
                    schema = @Schema(type = "string", pattern = REGEX_NICKNAME)
            )
            @RequestPart(name = "nickname")
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME, message = "회원 닉네임 서식이 올바르지 않습니다. ")
            String nickname,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) throws IOException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
                .body(DataResponse.ok(
                        memberController.overrideProfile(
                                new MemberProfileOverrideRecord(memberId, introduction, image, nickname))));
    }

    @Operation(
            summary = "게시글 좋아요 API",
            description = "게시글에 좋아요를 누릅니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PutMapping("/members/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationPost(
            @Parameter(
                    description = "좋아요를 누를 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.likePost(new MemberPostLikeRecord(memberId, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "게시글 좋아요 취소 API",
            description = "게시글에 대한 좋아요를 취소합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @DeleteMapping("/members/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationPost(
            @Parameter(
                    description = "좋아요를 취소할 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.unlikePost(new MemberPostUnlikeRecord(memberId, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "게시글 북마크 API",
            description = "게시글에 북마크를 누릅니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PutMapping("/members/bookmark/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> bookmarkCommunicationPost(
            @Parameter(
                    description = "북마크를 누를 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.bookmarkPost(new MemberPostBookmarkRecord(memberId, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "게시글 북마크 취소 API",
            description = "게시글에 대한 북마크를 취소합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @DeleteMapping("/members/bookmark/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> cancelCommunicationPostBookmark(
            @Parameter(
                    description = "북마크를 취소할 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.cancelPostBookmark(new MemberPostBookmarkCancelRecord(memberId, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "댓글 좋아요 API",
            description = "댓글에 좋아요를 누릅니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PutMapping("/members/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationComment(
            @Parameter(
                    description = "좋아요를 누를 댓글의 게시글 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(
                    description = "좋아요를 누를 댓글의 경로",
                    example = "1.0.4",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.likeComment(new MemberCommentLikeRecord(memberId, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "댓글 좋아요 취소 API",
            description = "댓글에 대한 좋아요를 취소합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @DeleteMapping("/members/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationComment(
            @Parameter(
                    description = "좋아요를 취소할 댓글의 게시글 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(
                    description = "좋아요를 취소할 댓글의 경로",
                    example = "1.0.4",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.unlikeComment(new MemberCommentUnlikeRecord(memberId, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "건의 및 버그 제보 API",
            description = "건의 사항 또는 버그를 제보합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping(value = "/report/proposal-or-bug", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> reportProposalOrBug(
            @Parameter(description = "보고서 제목", example = "제보합니다!")
            @RequestPart(name = "title")
            String title,

            @Parameter(description = "보고서 내용", example = "이런 건의 사항을 드립니다.")
            @RequestPart(name = "content")
            String content,

            @Parameter(
                    description = "제보 관련 이미지",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart(name = "image", required = false)
            MultipartFile image,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) throws IOException {
        memberController.reportProposalOrBug(new ProposalOrBugReportRecord(memberId, title, content, image));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    // TODO: 관리자 API로 등록 요망
    @Hidden
    @Operation(
            summary = "건의 및 버그 제보 제거 API",
            description = "건의 사항 또는 버그 제보를 제거합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @DeleteMapping(value = "/admin/report/proposal-or-bug/{reportUlid}")
    public ResponseEntity<DataResponse<Void>> removeProposalOrBugReport(
            @Parameter(
                    description = "삭제할 보고서의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "보고서 식별자가 비어 있습니다.")
            String reportUlid) {
        memberController.removeProposalOrBug(new ProposalOrBugReportRemoveRecord(reportUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Hidden
    @Operation(
            summary = "게시글 신고 API",
            description = "게시글을 신고합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping(value = "/report/abuse/post/")
    public ResponseEntity<DataResponse<Void>> reportPostAbuse(
            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID ignoredMemberId) {
        return ResponseEntity.badRequest().body(DataResponse.of(MemberErrorCode.NOT_FOUND_TARGET_POST_ID));
    }

    @Operation(
            summary = "게시글 신고 API",
            description = "게시글을 신고합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping(value = "/report/abuse/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> reportPostAbuse(
            @Parameter(
                    description = "신고할 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.reportPostAbuse(new PostAbuseReportRecord(memberId, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Hidden
    @Operation(
            summary = "댓글 신고 API",
            description = "댓글을 신고합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping(value = "/report/abuse/post//path/{path}")
    public ResponseEntity<DataResponse<Void>> reportCommentAbuse(
            @Parameter(
                    description = "신고할 댓글의 경로",
                    example = "1.0.4",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String ignoredPath,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID ignoredMemberId) {
        return ResponseEntity.badRequest().body(DataResponse.of(MemberErrorCode.NOT_FOUND_TARGET_COMMENT_ID));
    }

    @Operation(
            summary = "댓글 신고 API",
            description = "댓글을 신고합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping(value = "/report/abuse/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> reportCommentAbuse(
            @Parameter(
                    description = "신고할 댓글이 달린 게시글의 식별자",
                    schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID)
            )
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(
                    description = "신고할 댓글의 경로",
                    example = "1.0.4",
                    schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH)
            )
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        memberController.reportCommentAbuse(new CommentAbuseReportRecord(memberId, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "회원 탈퇴 API",
            description = "회원을 탈퇴합니다.",
            security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    )
    @PostMapping("/members")
    public ResponseEntity<DataResponse<Void>> withdrawMember(
            @RequestBody @Valid
            MemberWithdrawRequest request,

            @Parameter(hidden = true)
            @RequestHeader(name = HttpHeaders.AUTHORIZATION)
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        String accessToken = getTokenFromAuthorizationHeader(auth);
        memberController.withdraw(
                new MemberWithdrawalRecord(
                        request.authCode(),
                        request.authProvider(),
                        request.reason(),
                        request.opinion(),
                        accessToken));
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}