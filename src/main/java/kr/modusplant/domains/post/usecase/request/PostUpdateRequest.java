package kr.modusplant.domains.post.usecase.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostUpdateRequest(
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String ulid,

        Integer primaryCategoryId,

        Integer secondaryCategoryId,

        String title,

        List<MultipartFile> content,

        List<FileOrder> orderInfo,

        String thumbnailFilename,

        @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
        Boolean isPublished
) {
}