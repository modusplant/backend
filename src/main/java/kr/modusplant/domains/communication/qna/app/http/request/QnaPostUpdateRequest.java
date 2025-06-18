package kr.modusplant.domains.communication.qna.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationTitle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record QnaPostUpdateRequest(
        @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        @NotBlank(message = "식별자가 비어 있습니다.")
        String ulid,

        @Schema(description = "갱신을 위한 게시글 항목 식별자", example = "bde79fd5-083d-425c-b71b-69a157fc5739")
        @NotNull(message = "식별자가 비어 있습니다.")
        UUID categoryUuid,

        @Schema(description = "갱신을 위한 게시글 제목", example = "이거 과습인지 아시는 분!")
        @CommunicationTitle
        String title,

        @Schema(description = "갱신을 위한 게시글 컨텐츠")
        @NotNull(message = "게시글이 비어 있습니다.")
        List<MultipartFile> content,

        @Schema(description = "갱신을 위한 게시글 순서 정보")
        @NotNull(message = "순서 정보가 비어 있습니다.")
        List<FileOrder> orderInfo) {
}