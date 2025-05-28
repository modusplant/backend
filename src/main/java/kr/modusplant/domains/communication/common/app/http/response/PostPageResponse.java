package kr.modusplant.domains.communication.common.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

public record PostPageResponse<T> (
        List<T> posts,
        int page,
        int size,
        @JsonProperty("total_elements")
        long totalElements,
        @JsonProperty("total_pages")
        int totalPages,
        @JsonProperty("has_next")
        boolean hasNext,
        @JsonProperty("has_previous")
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

