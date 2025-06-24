package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationPath;

public record TipCommentInsertRequest(
        @Schema(
                description = "해당 댓글이 달린 게시글의 식별자",
                example = "01JXEDEX5GJNBB9SAB7FB2ZG9W"
        )
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String postUlid,

        @Schema(
                description = "댓글의 구체화된 경로",
                pattern = "^\\d+(?:\\.\\d+)*$",
                example = "2.4"
        )
        @CommunicationPath
        String path,

        @Schema(
                description = "댓글 컨텐츠",
                example = "좋은 정보 감사합니다!"
        )
        @NotBlank(message = "댓글이 비어 있습니다.")
        String content) {
}