package kr.modusplant.domains.comment.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "컨텐츠 댓글 API", description = "컨텐츠 댓글 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/communication/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentRestController {

    private final CommentController controller;

    /**
     * 소통 상세 페이지에 띄워지는 댓글에 대한 API입니다.
     * @param postUlid 댓글이 등록된 게시글의 식별자입니다.
     * @return 게시글에 등록된 댓글을 화면에 띄우기 위한 CommentResponse 를 반환합니다.
     */
    @Operation(
            summary = "게시글 식별자로 컨텐츠 댓글 조회 API",
            description = "게시글 식별자에 맞는 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<CommentOfPostResponse>>> gatherByPost(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @RequestHeader(name = HttpHeaders.IF_NONE_MATCH, required = false)
            String ifNoneMatch,

            @Parameter(hidden = true)
            @RequestHeader(name = HttpHeaders.IF_MODIFIED_SINCE, required = false)
            String ifModifiedSince
            ) {
        CommentCacheData cacheData = controller.getCacheData(postUlid, ifNoneMatch, ifModifiedSince);
        if (cacheData.result()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate())
                    .eTag(String.format("W/\"%s\"", cacheData.entityTag()))
                    .lastModified(
                            ZonedDateTime.of(
                                    (cacheData.lastModifiedAt()),
                                    ZoneId.of("Asia/Seoul")))
                    .build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate())
                    .eTag(String.format("W/\"%s\"", cacheData.entityTag()))
                    .lastModified(
                            ZonedDateTime.of(
                                    (cacheData.lastModifiedAt()),
                                    ZoneId.of("Asia/Seoul")))
                    .body(DataResponse.ok(controller.gatherByPost(postUlid)));
        }
    }

    /**
     * 마이페이지의 내 댓글 보기에 사용되는 API 입니다.
     * @param page 사용자가 현재 위치한 페이지의 번호입니다.
     * @param size 한 페이지 당 들어가는 댓글들의 수 입니다.
     * @return 페이지네이션 된 댓글 데이터들을 반환합니다.
     */
    @Operation(
            summary = "인가 회원 식별자로 컨텐츠 댓글 조회 API",
            description = "인가 회원 식별자에 맞는 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<CommentPageResponse<CommentOfAuthorPageModel>>> gatherByAuthor(
            @Parameter(schema = @Schema(
                    description = "댓글을 작성한 사용자의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @PathVariable(required = false, value = "uuid")
            @NotNull(message = "댓글을 작성한 사용자의 식별자가 비어 있습니다.")
            UUID memberUuid,

            @Parameter(
                    description = "현재 페이지의 숫자",
                    example = "0"
            )
            @RequestParam(value = "page", defaultValue = "0")
            int page,

            @Parameter(
                    description = "페이지 당 들어갈 댓글의 수",
                    example = "8"
            )
            @RequestParam(value = "size", defaultValue = "8")
            int size
    ) {
        CommentPageResponse<CommentOfAuthorPageModel> commentResponses =
                controller.gatherByAuthor(memberUuid, PageRequest.of(page, size));
        return ResponseEntity.ok().body(DataResponse.ok(commentResponses));
    }

    @Operation(
            summary = "컨텐츠 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 컨텐츠 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<Void>> register(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @RequestBody @Valid
            CommentRegisterRequest registerRequest) {
        UUID currentMemberUuid = userDetails.getActiveUuid();
        controller.register(registerRequest, currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "식별자로 컨텐츠 댓글 제거 API",
            description = "식별자로 컨텐츠 댓글을 제거합니다."
    )
    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> delete(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable
            String ulid,

            @Parameter(schema = @Schema(description = "댓글의 경로", example = "4.8.12"))
            @PathVariable
            String path
    ) {
        controller.delete(ulid, path);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
