package kr.modusplant.domains.communication.qna.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaLikeApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Q&A 좋아요 API", description = "Q&A 게시글 좋아요를 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/qna/posts")
@RequiredArgsConstructor
public class QnaLikeController {

    private final QnaLikeApplicationService qnaLikeApplicationService;

    // TODO : Spring Security 적용 후 SecurityUtil의 회원ID 사용
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(
            summary = "Q&A 게시글 좋아요 API",
            description = "Q&A 게시글 좋아요 기능"
    )
    @PostMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> likeQnaPost(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(qnaLikeApplicationService.likeQnaPost(ulid, memberUuid)));
    }

    @Operation(
            summary = "Q&A 게시글 좋아요 취소 API",
            description = "Q&A 게시글 좋아요 취소 기능"
    )
    @DeleteMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> unlikeQnaPost(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(qnaLikeApplicationService.unlikeQnaPost(ulid, memberUuid)));
    }

}
