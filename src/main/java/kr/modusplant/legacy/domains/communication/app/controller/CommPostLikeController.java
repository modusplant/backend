package kr.modusplant.legacy.domains.communication.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import kr.modusplant.legacy.domains.communication.app.http.response.CommPostLikeResponse;
import kr.modusplant.legacy.domains.communication.app.service.CommPostLikeApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "소통 게시글 좋아요 API", description = "소통 게시글 좋아요를 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/communication/posts")
@RequiredArgsConstructor
@Validated
public class CommPostLikeController {

    private final CommPostLikeApplicationService commPostLikeApplicationService;

    // TODO : Spring Security 적용 후 SecurityUtil의 회원ID 사용
    @Value("${TEMP_USER_UUID}")
    private UUID memberUuid;

    @Operation(
            summary = "소통 게시글 좋아요 API",
            description = "소통 게시글 좋아요 기능"
    )
    @PostMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<CommPostLikeResponse>> likeCommPost(
            @Parameter(schema = @Schema(
                    description = "좋아요를 누를 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(commPostLikeApplicationService.likeCommPost(ulid, memberUuid)));
    }

    @Operation(
            summary = "소통 게시글 좋아요 취소 API",
            description = "소통 게시글 좋아요 취소 기능"
    )
    @DeleteMapping("/{ulid}/like")
    public ResponseEntity<DataResponse<CommPostLikeResponse>> unlikeCommPost(
            @Parameter(schema = @Schema(
                    description = "좋아요를 취소할 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(commPostLikeApplicationService.unlikeCommPost(ulid, memberUuid)));
    }

}
