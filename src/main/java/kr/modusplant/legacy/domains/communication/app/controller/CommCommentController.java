package kr.modusplant.legacy.domains.communication.app.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.app.service.CommCommentApplicationService;
import kr.modusplant.legacy.domains.communication.domain.validation.CommunicationPath;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "컨텐츠 댓글 API", description = "컨텐츠 댓글 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/communication/comments")
@RequiredArgsConstructor
@Validated
public class CommCommentController {

    private final CommCommentApplicationService commentApplicationService;
    private final TokenProvider tokenProvider;

    @Operation(
            summary = "전체 컨텐츠 댓글 조회 API",
            description = "전체 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<CommCommentResponse>>> getAllCommComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @Operation(
            summary = "게시글 식별자로 컨텐츠 댓글 조회 API",
            description = "게시글 식별자에 맞는 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<CommCommentResponse>>> getByPost(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        CommPostEntity postEntity = CommPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @Operation(
            summary = "인가 회원 식별자로 컨텐츠 댓글 조회 API",
            description = "인가 회원 식별자에 맞는 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<CommCommentResponse>>> getByAuthMember(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @PathVariable(required = false, value = "uuid")
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID authMemberUuid) {
        SiteMemberEntity authMemberEntity = SiteMemberEntity.builder().uuid(authMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByAuthMember(authMemberEntity)));
    }

    @Operation(
            summary = "작성 회원 식별자로 컨텐츠 댓글 조회 API",
            description = "작성 회원 식별자에 맞는 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<CommCommentResponse>>> getByCreateMember(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @PathVariable(required = false, value = "uuid")
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @Operation(
            summary = "게시글 식별자와 경로로 컨텐츠 댓글 조회 API",
            description = "게시글 식별자와 경로에 맞는 컨텐츠 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(schema = @Schema(
                    description = "댓글의 구체화된 경로",
                    pattern = "^\\d+(?:\\.\\d+)*$",
                    example = "4.8.12")
            )
            @PathVariable(required = false, value = "path")
            @CommunicationPath
            String path) {
        Optional<CommCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, path);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "컨텐츠 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 컨텐츠 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<CommCommentResponse>> insertCommComment(
            @Parameter(schema = @Schema(
                    description = "회원의 접근 토큰")
            )
            @RequestHeader("Authorization")
            @NotBlank(message = "접근 토큰이 비어 있습니다.")
            String rawAccessToken,
            @RequestBody @Valid
            CommCommentInsertRequest insertRequest) {
        Claims accessTokenClaims = tokenProvider.getClaimsFromToken(rawAccessToken.substring(7));
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService
                .insert(insertRequest, UUID.fromString(accessTokenClaims.getSubject()))
        ));
    }

    @Operation(
            summary = "식별자로 컨텐츠 댓글 제거 API",
            description = "식별자로 컨텐츠 댓글을 제거합니다."
    )
    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeCommComment(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(schema = @Schema(
                    description = "댓글의 구체화된 경로",
                    pattern = "^\\d+(?:\\.\\d+)*$",
                    example = "4.8.12")
            )
            @PathVariable(required = false, value = "path")
            @CommunicationPath
            String path) {
        commentApplicationService.removeByPostUlidAndPath(postUlid, path);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
