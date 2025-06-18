package kr.modusplant.domains.communication.conversation.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvCommentApplicationService;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    // 임시로 Spring Security 적용 전 인증 우회를 위해 사용
    // gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용
    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

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
            @PathVariable(required = false, value = "uuid")
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @Operation(
            summary = "컨텐츠로 대화 댓글 조회 API",
            description = "컨텐츠에 맞는 대화 댓글을 조회합니다."
    )
    @GetMapping("/content/{content}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByContent(
            @PathVariable(required = false, value = "content")
            @NotBlank(message = "컨텐츠가 비어 있습니다.")
            String content) {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByContent(content)));
    }

    @Operation(
            summary = "게시글 식별자와 경로로 대화 댓글 조회 API",
            description = "게시글 식별자와 경로에 맞는 대화 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath(
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @PathVariable(required = false, value = "path")
            @NotBlank(message = "경로가 비어 있습니다.")
            String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        Optional<ConvCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, decodedPath);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "대화 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 대화 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<ConvCommentResponse>> insertConvComment(@RequestBody @Valid ConvCommentInsertRequest insertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService.insert(insertRequest, memberUuid)));
    }

    @Operation(
            summary = "식별자로 대화 댓글 제거 API",
            description = "식별자로 대화 댓글을 제거합니다."
    )
    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeConvComment(
            @PathVariable(required = false, value = "ulid")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @PathVariable(required = false, value = "path")
            @NotBlank(message = "경로가 비어 있습니다.")
            String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        commentApplicationService.removeByPostUlidAndPath(postUlid, decodedPath);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
