package kr.modusplant.domains.communication.qna.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationPath;
import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaCommentApplicationService;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Q&A 댓글 API", description = "Q&A 댓글 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/qna/comments")
@RequiredArgsConstructor
@Validated
public class QnaCommentController {

    private final QnaCommentApplicationService commentApplicationService;

    // 임시로 Spring Security 적용 전 인증 우회를 위해 사용
    // gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용
    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(
            summary = "전체 Q&A 댓글 조회 API",
            description = "전체 Q&A 댓글을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getAllQnaComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @Operation(
            summary = "게시글 식별자로 Q&A 댓글 조회 API",
            description = "게시글 식별자에 맞는 Q&A 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByPost(
            @Parameter(schema = @Schema(
                    description = "해당 댓글이 달린 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        QnaPostEntity postEntity = QnaPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @Operation(
            summary = "인가 회원 식별자로 Q&A 댓글 조회 API",
            description = "인가 회원 식별자에 맞는 Q&A 댓글을 조회합니다."
    )
    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByAuthMember(
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
            summary = "작성 회원 식별자로 Q&A 댓글 조회 API",
            description = "작성 회원 식별자에 맞는 Q&A 댓글을 조회합니다."
    )
    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByCreateMember(
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
            summary = "게시글 식별자와 경로로 Q&A 댓글 조회 API",
            description = "게시글 식별자와 경로에 맞는 Q&A 댓글을 조회합니다."
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
        Optional<QnaCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, path);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "Q&A 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 Q&A 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<QnaCommentResponse>> insertQnaComment(@RequestBody @Valid QnaCommentInsertRequest insertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService.insert(insertRequest, memberUuid)));
    }

    @Operation(
            summary = "식별자로 Q&A 댓글 제거 API",
            description = "식별자로 Q&A 댓글을 제거합니다."
    )
    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeQnaComment(
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
