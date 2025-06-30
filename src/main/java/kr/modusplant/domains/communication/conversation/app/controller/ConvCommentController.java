package kr.modusplant.domains.communication.conversation.app.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationPath;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvCommentApplicationService;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "대화 댓글 API", description = "대화 댓글 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/conversation/comments")
@RequiredArgsConstructor
@Validated
public class ConvCommentController {

    private final ConvCommentApplicationService commentApplicationService;
    private final TokenProvider tokenProvider;

    @Operation(
            summary = "전체 대화 댓글 조회 API",
            description = "전체 대화 댓글을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getAllConvComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @Operation(
            summary = "게시글 식별자로 대화 댓글 조회 API",
            description = "게시글 식별자에 맞는 대화 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByPost(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        ConvPostEntity postEntity = ConvPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @Operation(
            summary = "인가 회원 식별자로 대화 댓글 조회 API",
            description = "인가 회원 식별자에 맞는 대화 댓글을 조회합니다."
    )
    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByAuthMember(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "fcf1a3d0-45a2-4490-bbef-1f5bff40c5bc")
            )
            @PathVariable(required = false, value = "uuid")
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID authMemberUuid) {
        SiteMemberEntity authMemberEntity = SiteMemberEntity.builder().uuid(authMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByAuthMember(authMemberEntity)));
    }

    @Operation(
            summary = "작성 회원 식별자로 대화 댓글 조회 API",
            description = "작성 회원 식별자에 맞는 대화 댓글을 조회합니다."
    )
    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByCreateMember(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "fcf1a3d0-45a2-4490-bbef-1f5bff40c5bc")
            )
            @PathVariable(required = false, value = "uuid")
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @Operation(
            summary = "게시글 식별자와 경로로 대화 댓글 조회 API",
            description = "게시글 식별자와 경로에 맞는 대화 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(schema = @Schema(
                    description = "댓글의 구체화된 경로",
                    pattern = "^\\d+(?:\\.\\d+)*$",
                    example = "7.2.3")
            )
            @PathVariable(required = false, value = "path")
            @CommunicationPath
            String path) {
        Optional<ConvCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, path);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "대화 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 대화 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<ConvCommentResponse>> insertConvComment(
            @Parameter(schema = @Schema(
                    description = "회원의 접근 토큰")
            )
            @RequestHeader("Authorization")
            @NotBlank(message = "접근 토큰이 비어 있습니다.")
            String rawAccessToken,
            @RequestBody @Valid
            ConvCommentInsertRequest insertRequest) {
        Claims accessTokenClaims = tokenProvider.getClaimsFromToken(rawAccessToken.substring(7));
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService
                .insert(insertRequest, UUID.fromString(accessTokenClaims.getSubject()))
        ));
    }

    @Operation(
            summary = "식별자로 대화 댓글 제거 API",
            description = "식별자로 대화 댓글을 제거합니다."
    )
    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeConvComment(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(schema = @Schema(
                    description = "댓글의 구체화된 경로",
                    pattern = "^\\d+(?:\\.\\d+)*$",
                    example = "7.2.3")
            )
            @PathVariable(required = false, value = "path")
            @CommunicationPath
            String path) {
        commentApplicationService.removeByPostUlidAndPath(postUlid, path);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
