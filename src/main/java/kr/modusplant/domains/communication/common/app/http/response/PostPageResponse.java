package kr.modusplant.domains.communication.common.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record PostPageResponse<T> (
        @Schema(description = "조회된 포스트")
        List<T> posts,

        @Schema(description = "현재 페이지 번호(0부터 시작)", example = "2")
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
){
    public static <T> PostPageResponse<T> from(Page<T> page) {
        return new PostPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}

