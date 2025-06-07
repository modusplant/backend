package kr.modusplant.domains.communication.conversation.app.controller;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvCommentApplicationService;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
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

@RestController
@Primary
@RequestMapping("/api/v1/conversation/comments")
@RequiredArgsConstructor
public class ConvCommentController {

    private final ConvCommentApplicationService commentApplicationService;

    @GetMapping
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getAllConvComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByPost(@PathVariable("ulid") String ulid) {
        ConvPostEntity postEntity = ConvPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByAuthMember(@PathVariable("uuid") UUID authMemberUuid) {
        SiteMemberEntity authMemberEntity = SiteMemberEntity.builder().uuid(authMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByAuthMember(authMemberEntity)));
    }

    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByCreateMember(@PathVariable("uuid") UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @GetMapping("/content/{content}")
    public ResponseEntity<DataResponse<List<ConvCommentResponse>>> getByContent(@PathVariable("content") String content) {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByContent(content)));
    }

    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath
            (@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        Optional<ConvCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, decodedPath);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @PostMapping
    public ResponseEntity<DataResponse<ConvCommentResponse>> insertConvComment(@RequestBody ConvCommentInsertRequest insertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService.insert(insertRequest)));
    }

    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeConvComment(@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        commentApplicationService.removeByPostUlidAndPath(postUlid, decodedPath);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
