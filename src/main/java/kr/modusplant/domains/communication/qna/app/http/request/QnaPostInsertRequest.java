package kr.modusplant.domains.communication.qna.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record QnaPostInsertRequest(
        @Schema(description = "게시글이 포함된 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548")
        UUID categoryUuid,

        @Schema(description = "게시글의 제목", example = "이거 과습인가요?")
        String title,

        @Schema(description = "게시글 컨텐츠")
        List<MultipartFile> content,

        @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보")
        List<FileOrder> orderInfo) {
}