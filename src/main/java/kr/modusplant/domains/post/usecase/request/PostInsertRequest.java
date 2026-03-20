package kr.modusplant.domains.post.usecase.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostInsertRequest(
        Integer primaryCategoryId,

        Integer secondaryCategoryId,

        String title,

        List<MultipartFile> content,

        List<FileOrder> orderInfo,

        @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
        Boolean isPublished
) {
}