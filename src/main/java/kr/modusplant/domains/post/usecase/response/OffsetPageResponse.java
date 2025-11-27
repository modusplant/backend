package kr.modusplant.domains.post.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.post.usecase.response.supers.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public record OffsetPageResponse<T> (
        @Schema(description = "조회된 포스트")
        List<T> posts,

        @Schema(description = "현재 페이지 번호(1부터 시작)", example = "2")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size,

        @Schema(description = "포스트의 총 개수", example = "62")
        long totalElements,

        @Schema(description = "페이지의 총 개수", example = "7")
        int totalPages,

        @Schema(description = "다음 페이지가 존재하는지", example = "true")
        boolean hasNext,

        @Schema(description = "이전 페이지가 존재하는지", example = "true")
        boolean hasPrevious
) implements PageResponse<T> {
    public static <T> OffsetPageResponse<T> from(Page<T> page) {
        return new OffsetPageResponse<>(
                page.getContent(),
                page.getNumber()+1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}

