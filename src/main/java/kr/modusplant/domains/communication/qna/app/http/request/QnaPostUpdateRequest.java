package kr.modusplant.domains.communication.qna.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record QnaPostUpdateRequest(
        @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        String ulid,

        @Schema(description = "갱신을 위한 게시글 항목 식별자", example = "bde79fd5-083d-425c-b71b-69a157fc5739")
        UUID categoryUuid,

        @Schema(description = "갱신을 위한 게시글 제목", example = "이거 과습인지 아시는 분!")
        String title,

        @Schema(description = "갱신을 위한 게시글 컨텐츠")
        List<MultipartFile> content,

        @Schema(description = "갱신을 위한 게시글 순서 정보")
        List<FileOrder> orderInfo) {
}