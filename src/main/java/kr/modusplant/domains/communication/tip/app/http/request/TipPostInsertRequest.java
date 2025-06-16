package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record TipPostInsertRequest(
        @Schema(description = "게시글이 포함된 항목의 식별자", example = "9c200e7d-77c7-41c1-abd0-87e3b9e49c40")
        UUID categoryUuid,

        @Schema(description = "게시글의 제목", example = "흙이 마른 것을 쉽게 확인할 수 있는 방법")
        String title,

        @Schema(description = "게시글 컨텐츠")
        List<MultipartFile> content,

        @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보")
        List<FileOrder> orderInfo) {
}