package kr.modusplant.domains.communication.conversation.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvLikeApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Conv Like API")
@RestController
@RequestMapping("/api/v1/conversation/posts")
@RequiredArgsConstructor
public class ConvLikeController {

    private final ConvLikeApplicationService convLikeApplicationService;

    // TODO : Spring Security 적용 후 SecurityUtil의 회원ID 사용
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(summary = "대화 게시글 좋아요 API", description = "대화 게시글 좋아요 기능")
    @PostMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> likeConvPost(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convLikeApplicationService.likeConvPost(ulid, memberUuid)));
    }

    @Operation(summary = "대화 게시글 좋아요 취소 API", description = "대화 게시글 좋아요 취소 기능")
    @DeleteMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> unlikeConvPost(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convLikeApplicationService.unlikeConvPost(ulid, memberUuid)));
    }

}
