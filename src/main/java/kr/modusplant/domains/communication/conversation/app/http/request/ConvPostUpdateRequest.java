package kr.modusplant.domains.communication.conversation.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record ConvPostUpdateRequest(
        @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        String ulid,

        @Schema(description = "갱신을 위한 게시글 항목 식별자", example = "12941529-1ecf-4f9c-afe0-6c2be065bf8d")
        UUID categoryUuid,

        @Schema(description = "갱신을 위한 게시글 제목", example = "우리 집 식물을 공개합니다!")
        String title,

        @Schema(description = "갱신을 위한 게시글 컨텐츠")
        List<MultipartFile> content,

        @Schema(description = "갱신을 위한 게시글 순서 정보")
        List<FileOrder> orderInfo) {
}