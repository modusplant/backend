package kr.modusplant.domains.communication.conversation.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvLikeApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "대화 좋아요 API", description = "대화 게시글 좋아요를 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/conversation/posts")
@RequiredArgsConstructor
@Validated
public class ConvLikeController {

    private final ConvLikeApplicationService convLikeApplicationService;

    // TODO : Spring Security 적용 후 SecurityUtil의 회원ID 사용
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(
            summary = "대화 게시글 좋아요 API",
            description = "대화 게시글에 좋아요를 표시합니다."
    )
    @PostMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> likeConvPost(
            @Parameter(schema = @Schema(
                    description = "좋아요를 누를 게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convLikeApplicationService.likeConvPost(ulid, memberUuid)));
    }

    @Operation(
            summary = "대화 게시글 좋아요 취소 API",
            description = "대화 게시글에 표시한 좋아요를 취소합니다."
    )
    @DeleteMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<LikeResponse>> unlikeConvPost(
            @Parameter(schema = @Schema(
                    description = "좋아요를 취소할 게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convLikeApplicationService.unlikeConvPost(ulid, memberUuid)));
    }

}
