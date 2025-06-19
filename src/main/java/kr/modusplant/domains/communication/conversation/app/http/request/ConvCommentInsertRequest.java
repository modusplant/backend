package kr.modusplant.domains.communication.conversation.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationPath;

public record ConvCommentInsertRequest(
        @Schema(
                description = "해당 댓글이 달린 게시글의 식별자",
                example = "01ARZ3NDEKTSV4RRFFQ69G5FAV"
        )
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String postUlid,

        @Schema(
                description = "댓글의 구체화된 경로",
                pattern = "^\\d+(?:\\.\\d+)*$",
                example = "1.5.12"
        )
        @CommunicationPath
        String path,

        @Schema(
                description = "댓글 컨텐츠",
                example = "식물이 너무 예뻐요!"
        )
        @NotBlank(message = "댓글이 비어 있습니다.")
        String content) {
}