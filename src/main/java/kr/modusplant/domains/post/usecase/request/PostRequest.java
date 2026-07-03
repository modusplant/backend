package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;

public record PostRequest(
        @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "1")
        Integer primaryCategoryId,

        @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "1")
        Integer secondaryCategoryId,

        @Schema(description = "게시글의 제목", maximum = "60", example = "이거 과습인가요?")
        String title,

        @Schema(description = "게시글의 컨텐츠 텍스트 내용", example = "안녕하세요. 이 식물 과습인가요?")
        String contentText,

        @Schema(description = "게시글의 컨텐츠 내용 파일 정보")
        List<@Valid FileOrder> contentFiles,

        @Schema(description = "게시글 컨텐츠 대표 사진의 파일명", example = "image_0.jpg")
        String thumbnailFilename
) {
}