package kr.modusplant.domains.communication.qna.app.controller;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaCommentApplicationService;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
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
@RequestMapping("/api/v1/qna/comments")
@RequiredArgsConstructor
public class QnaCommentController {

    private final QnaCommentApplicationService commentApplicationService;

    @GetMapping
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getAllQnaComment() {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getAll()));
    }

    @GetMapping("/post/{ulid}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByPost(@PathVariable("ulid") String ulid) {
        QnaPostEntity postEntity = QnaPostEntity.builder().ulid(ulid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByPostEntity(postEntity)));
    }

    @GetMapping("/member/auth/{uuid}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByAuthMember(@PathVariable("uuid") UUID authMemberUuid) {
        SiteMemberEntity authMemberEntity = SiteMemberEntity.builder().uuid(authMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByAuthMember(authMemberEntity)));
    }

    @GetMapping("/member/create/{uuid}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByCreateMember(@PathVariable("uuid") UUID createMemberUuid) {
        SiteMemberEntity createMemberEntity = SiteMemberEntity.builder().uuid(createMemberUuid).build();

        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByCreateMember(createMemberEntity)));
    }

    @GetMapping("/content/{content}")
    public ResponseEntity<DataResponse<List<QnaCommentResponse>>> getByContent(@PathVariable("content") String content) {
        return ResponseEntity.ok().body(
                DataResponse.ok(commentApplicationService.getByContent(content)));
    }

    @GetMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> getByPostAndPath
            (@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        Optional<QnaCommentResponse> optionalResponse = commentApplicationService
                .getByPostUlidAndPath(postUlid, decodedPath);

        return optionalResponse.isPresent() ?
                ResponseEntity.ok().body(DataResponse.ok(optionalResponse)) :
                ResponseEntity.ok().body(DataResponse.ok());
    }

    @PostMapping
    public ResponseEntity<DataResponse<QnaCommentResponse>> insertQnaComment(@RequestBody QnaCommentInsertRequest insertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commentApplicationService.insert(insertRequest)));
    }

    @DeleteMapping("/post/{ulid}/path/{path}")
    public ResponseEntity<DataResponse<?>> removeQnaComment(@PathVariable("ulid") String postUlid, @PathVariable("path") String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        commentApplicationService.removeByPostUlidAndPath(postUlid, decodedPath);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
