package kr.modusplant.legacy.domains.communication.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.legacy.domains.communication.domain.validation.CommunicationPath;

public record CommCommentInsertRequest(
        @Schema(
                description = "해당 댓글이 달린 게시글의 식별자",
                example = "01JXEDF9SNSMAVBY8Z3P5YXK5J"
        )
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String postUlid,

        @Schema(
                description = "댓글의 구체화된 경로",
                pattern = "^\\d+(?:\\.\\d+)*$",
                example = "1.2.3"
        )
        @CommunicationPath
        String path,

        @Schema(
                description = "댓글 컨텐츠",
                example = "궁금한 점이 해결되었어요 감사합니다!"
        )
        @NotBlank(message = "댓글이 비어 있습니다.")
        String content) {
}
