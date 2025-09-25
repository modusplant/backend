package kr.modusplant.domains.comment.adapter.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.legacy.domains.communication.domain.validation.CommunicationPath;

public record CommentDeleteRequest(
        @Parameter(schema = @Schema(
                description = "해당 댓글이 달린 게시글의 식별자",
                example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
        )
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String postUlid,

        @Parameter(schema = @Schema(
                description = "댓글의 구체화된 경로",
                pattern = "^\\d+(?:\\.\\d+)*$",
                example = "4.8.12")
        )
        @CommunicationPath
        String path
) {
}
