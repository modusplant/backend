package kr.modusplant.domains.post.usecase.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record PostInsertRequest(
        @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
        UUID primaryCategoryUuid,

        @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
        UUID secondaryCategoryUuid,

        @NotBlank(message = "게시글 제목이 비어 있습니다.")
        @Length(max = 60, message = "게시글 제목은 최대 60글자까지 작성할 수 있습니다.")
        String title,

        @NotNull(message = "게시글이 비어 있습니다.")
        List<MultipartFile> content,

        @NotNull(message = "순서 정보가 비어 있습니다.")
        List<FileOrder> orderInfo,

        @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
        Boolean isPublished
) {
}