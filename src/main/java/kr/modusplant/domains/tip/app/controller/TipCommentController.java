package kr.modusplant.domains.tip.app.controller;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.app.service.TipCommentApplicationService;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Primary
@RequestMapping("/api/crud/tip/comment")
@RequiredArgsConstructor
public class TipCommentController {

    private final TipCommentApplicationService commentApplicationService;

    @GetMapping
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getAllTipComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByPost(@PathVariable("ulid") String ulid) {
        TipPostEntity postEntity = TipPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByAuthMember(@PathVariable("uuid") UUID authMemberUuid) {
        SiteMemberEntity authMemberEntity = SiteMemberEntity.builder().uuid(authMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByAuthMember(authMemberEntity)));
    }

    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByCreateMember(@PathVariable("uuid") UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @GetMapping("/content/{content}")
    public ResponseEntity<DataResponse<List<TipCommentResponse>>> getByContent(@PathVariable("content") String content) {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByContent(content)));
    }

    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath
            (@PathVariable("ulid") String postUlid, @PathVariable("path") String materializedPath) {
        String decodedPath = URLDecoder.decode(materializedPath, StandardCharsets.UTF_8);

        Optional<TipCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndMaterializedPath(postUlid, decodedPath);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @PostMapping
    public ResponseEntity<DataResponse<TipCommentResponse>> insertTipComment(@RequestBody TipCommentInsertRequest insertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService.insert(insertRequest)));
    }

    @DeleteMapping("/{ulid}/{path}")
    public ResponseEntity<DataResponse<?>> removeTipComment(@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        commentApplicationService.removeByPostUlidAndMaterializedPath(postUlid, decodedPath);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
