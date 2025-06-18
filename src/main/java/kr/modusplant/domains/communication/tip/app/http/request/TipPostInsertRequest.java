package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationTitle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record TipPostInsertRequest(
        @Schema(description = "게시글이 포함된 항목의 식별자", example = "9c200e7d-77c7-41c1-abd0-87e3b9e49c40")
        UUID categoryUuid,

        @Schema(description = "게시글의 제목", example = "흙이 마른 것을 쉽게 확인할 수 있는 방법")
        @CommunicationTitle
        String title,

        @Schema(description = "게시글 컨텐츠")
        @NotNull(message = "게시글이 비어 있습니다.")
        List<MultipartFile> content,

        @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보")
        @NotNull(message = "순서 정보가 비어 있습니다.")
        List<FileOrder> orderInfo) {
}