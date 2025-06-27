package kr.modusplant.domains.communication.tip.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.tip.app.service.TipLikeApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "팁 좋아요 API", description = "팁 게시글 좋아요를 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/tip/posts")
@RequiredArgsConstructor
@Validated
public class TipLikeController {

    private final TipLikeApplicationService tipLikeApplicationService;

    // TODO : Spring Security 적용 후 SecurityUtil의 회원ID 사용
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(
            summary = "팁 게시글 좋아요 API",
            description = "팁 게시글 좋아요 기능"
    )
    @PostMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> likeTipPost(
            @Parameter(schema = @Schema(
                    description = "좋아요를 누를 게시글의 식별자",
                    example = "01JY3PPXRH2QVWT0B8CPKA4TS1")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(tipLikeApplicationService.likeTipPost(ulid, memberUuid)));
    }

    @Operation(
            summary = "팁 게시글 좋아요 취소 API",
            description = "팁 게시글 좋아요 취소 기능"
    )
    @DeleteMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> unlikeTipPost(
            @Parameter(schema = @Schema(
                    description = "좋아요를 취소할 게시글의 식별자",
                    example = "01JY3PPXRH2QVWT0B8CPKA4TS1")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(tipLikeApplicationService.unlikeTipPost(ulid, memberUuid)));
    }

}
