package kr.modusplant.domains.communication.tip.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.communication.tip.app.service.TipCommentApplicationService;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "팁 댓글 API", description = "팁 댓글 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/tip/comments")
@RequiredArgsConstructor
public class TipCommentController {

    private final TipCommentApplicationService commentApplicationService;

    @Operation(
            summary = "전체 팁 댓글 조회 API",
            description = "전체 팁 댓글을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getAllTipComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @Operation(
            summary = "게시글 식별자로 팁 댓글 조회 API",
            description = "게시글 식별자에 맞는 팁 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByPost(@PathVariable("ulid") String ulid) {
        TipPostEntity postEntity = TipPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @Operation(
            summary = "인가 회원 식별자로 팁 댓글 조회 API",
            description = "인가 회원 식별자에 맞는 팁 댓글을 조회합니다."
    )
    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByAuthMember(@PathVariable("uuid") UUID authMemberUuid) {
        SiteMemberEntity authMemberEntity = SiteMemberEntity.builder().uuid(authMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByAuthMember(authMemberEntity)));
    }

    @Operation(
            summary = "작성 회원 식별자로 팁 댓글 조회 API",
            description = "작성 회원 식별자에 맞는 팁 댓글을 조회합니다."
    )
    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByCreateMember(@PathVariable("uuid") UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @Operation(
            summary = "컨텐츠로 팁 댓글 조회 API",
            description = "컨텐츠에 맞는 팁 댓글을 조회합니다."
    )
    @GetMapping("/content/{content}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByContent(@PathVariable("content") String content) {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByContent(content)));
    }

    @Operation(
            summary = "게시글 식별자와 경로로 팁 댓글 조회 API",
            description = "게시글 식별자와 경로에 맞는 팁 댓글을 조회합니다."
    )
    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath
            (@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        Optional<TipCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, decodedPath);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "팁 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 팁 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<TipCommentResponse>> insertTipComment(@RequestBody TipCommentInsertRequest insertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService.insert(insertRequest)));
    }

    @Operation(
            summary = "식별자로 팁 댓글 제거 API",
            description = "식별자로 팁 댓글을 제거합니다."
    )
    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeTipComment(@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        commentApplicationService.removeByPostUlidAndPath(postUlid, decodedPath);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
