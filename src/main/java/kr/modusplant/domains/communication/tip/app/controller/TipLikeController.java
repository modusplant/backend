package kr.modusplant.domains.communication.tip.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.tip.app.http.response.TipLikeResponse;
import kr.modusplant.domains.communication.tip.app.service.TipLikeApplicationService;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Tip Like API")
@RestController
@RequestMapping("/api/v1/tip/posts")
@RequiredArgsConstructor
public class TipLikeController {

    private final TipLikeApplicationService tipLikeApplicationService;

    // TODO : Spring Security 적용 후 SecurityUtil의 회원ID 사용
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(summary = "팁 게시글 좋아요 API", description = "팁 게시글 좋아요 기능")
    @PostMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<TipLikeResponse>> likeTipPost(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(tipLikeApplicationService.likeTipPost(ulid, memberUuid)));
    }

    @Operation(summary = "팁 게시글 좋아요 취소 API", description = "팁 게시글 좋아요 취소 기능")
    @DeleteMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<TipLikeResponse>> unlikeTipPost(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(tipLikeApplicationService.unlikeTipPost(ulid, memberUuid)));
    }

}
