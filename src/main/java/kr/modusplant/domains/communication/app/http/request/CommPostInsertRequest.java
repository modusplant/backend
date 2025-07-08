package kr.modusplant.domains.communication.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.domain.validation.CommunicationTitle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record CommPostInsertRequest(
        @Schema(
                description = "게시글이 포함된 항목의 식별자",
                example = "148d6e33-102d-4df4-a4d0-5ff233665548"
        )
        @NotNull(message = "항목 식별자가 비어 있습니다.")
        UUID categoryUuid,

        @Schema(
                description = "게시글의 제목",
                maximum = "150",
                example = "이거 과습인가요?"
        )
        @CommunicationTitle
        String title,

        @Schema(description = "게시글 컨텐츠")
        @NotNull(message = "게시글이 비어 있습니다.")
        List<MultipartFile> content,

        @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보")
        @NotNull(message = "순서 정보가 비어 있습니다.")
        List<FileOrder> orderInfo) {
}