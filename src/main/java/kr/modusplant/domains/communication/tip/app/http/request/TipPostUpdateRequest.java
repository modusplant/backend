package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record TipPostUpdateRequest(
        @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01JXEDEX5GJNBB9SAB7FB2ZG9W")
        String ulid,

        @Schema(description = "갱신을 위한 게시글 항목 식별자", example = "601f7b88-f0cc-4142-ab8f-d09ce06e5317")
        UUID categoryUuid,

        @Schema(description = "갱신을 위한 게시글 제목", example = "이것만 있으면 흙이 마른 것을 바로 알 수 있습니다")
        String title,

        @Schema(description = "갱신을 위한 게시글 컨텐츠")
        List<MultipartFile> content,

        @Schema(description = "갱신을 위한 게시글 순서 정보")
        List<FileOrder> orderInfo) {
}