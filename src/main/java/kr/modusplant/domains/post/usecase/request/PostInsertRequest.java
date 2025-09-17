package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record PostInsertRequest(
        @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548")
        @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
        UUID primaryCategoryUuid,

        @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "533d1106-c4b9-4c87-8536-a182f3cac4a0")
        @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
        UUID secondaryCategoryUuid,

        @Schema(description = "게시글의 제목", maximum = "150", example = "이거 과습인가요?"
        )
        @NotBlank(message = "게시글 제목이 비어 있습니다.")
        @Length(max = 150, message = "게시글 제목은 최대 150글자까지 작성할 수 있습니다.")
        String title,

        @Schema(description = "게시글 컨텐츠")
        @NotNull(message = "게시글이 비어 있습니다.")
        List<MultipartFile> content,

        @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보")
        @NotNull(message = "순서 정보가 비어 있습니다.")
        List<FileOrder> orderInfo,

        @Schema(description = "게시글을 발행 유무에 대한 정보")
        @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
        Boolean isPublished
) {
}