package kr.modusplant.domains.communication.conversation.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationTitle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record ConvPostInsertRequest(
        @Schema(
                description = "게시글이 포함된 항목의 식별자",
                example = "5f989e30-b0b8-4e59-a733-f7b5c8d901f8"
        )
        @NotNull(message = "항목 식별자가 비어 있습니다.")
        UUID categoryUuid,

        @Schema(
                description = "게시글의 제목",
                maximum = "150",
                example = "우리 집 식물 구경하세요~"
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