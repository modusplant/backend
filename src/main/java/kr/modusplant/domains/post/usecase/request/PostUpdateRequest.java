package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record PostUpdateRequest(
        @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String ulid,

        @Schema(description = "갱신을 위한 1차 항목 식별자", example = "3566cbd8-069a-4c9d-980f-74a2599a4413")
        @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
        UUID primaryCategoryUuid,

        @Schema(description = "갱신을 위한 2차 항목 식별자", example = "4d811fd8-462f-46f8-911f-71b5f80283da")
        @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
        UUID secondaryCategoryUuid,

        @Schema(description = "갱신을 위한 게시글 제목", example = "이거 과습인지 아시는 분!")
        @NotBlank(message = "게시글 제목이 비어 있습니다.")
        @Length(max = 150, message = "게시글 제목은 최대 150글자까지 작성할 수 있습니다.")
        String title,

        @Schema(description = "갱신을 위한 게시글 컨텐츠")
        @NotNull(message = "컨텐츠가 비어 있습니다.")
        List<MultipartFile> content,

        @Schema(description = "갱신을 위한 게시글 순서 정보")
        @NotNull(message = "순서 정보가 비어 있습니다.")
        List<FileOrder> orderInfo,

        @Schema(description = "게시글을 발행 유무에 대한 정보")
        @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
        Boolean isPublished
) {
}