package kr.modusplant.domains.comment.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentRegisterRequest(
        @Schema(
                description = "댓글이 등록된 게시글의 식별자",
                example = "01JY3PPG5YJ41H7BPD0DSQW2RD"
        )
        @NotBlank(message = "게시글의 식별자가 비어 있습니다.")
        String postId,

        @Schema(
                description = "댓글이 위치한 경로",
                pattern = "^\\d+(?:\\.\\d+)*$",
                example = "4.8.12"
        )
        @NotBlank(message = "댓글의 경로가 비어 있습니다.")
        String path,

        @Schema(
                description = "댓글을 작성한 사용자의 식별자",
                example = "038ae842-3c93-484f-b526-7c4645a195a7"
        )
        @NotNull(message = "댓글 작성자의 식별자가 비어 있습니다.")
        UUID memberUuid,

        @Schema(
                description = "댓글의 내용",
                example = "테스트 댓글 내용입니다"
        )
        @NotBlank(message = "댓글의 내용이 비어 있습니다.")
        String content
) {
}
