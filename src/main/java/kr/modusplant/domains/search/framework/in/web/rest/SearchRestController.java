package kr.modusplant.domains.search.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.search.adapter.controller.SearchController;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.usecase.record.SearchPostRecord;
import kr.modusplant.domains.search.usecase.response.SearchPostRelevanceSortedPageResponse;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.REGEX_ULID;

@Tag(name = "검색 API", description = "검색 기능을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class SearchRestController {
    private final SearchController searchController;

    @Operation(
            summary = "키워드를 통한 게시글 목록 검색 API (무한스크롤)",
            description = "키워드별 게시글 컨텐츠의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/posts")
    public ResponseEntity<DataResponse<SearchPostRelevanceSortedPageResponse<SearchPostResponse>>> searchPostsByKeyword(
            @Parameter(schema = @Schema(description = "검색어 키워드", example = "벌레"))
            @RequestParam
            @NotBlank(message = "키워드가 비어 있습니다.")
            String keyword,

            @Parameter(schema = @Schema(description = "검색 타겟", example = "title_content"))
            @RequestParam
            SearchPostTarget searchPostTarget,

            @Parameter(schema = @Schema(description = "검색 정렬 조건", example = "latest"))
            @RequestParam
            SearchPostSortCondition searchPostSortCondition,

            @Parameter(schema = @Schema(description = "1차 항목 식별자", example = "1"))
            @RequestParam(required = false)
            Integer primaryCategoryId,

            @Parameter(schema = @Schema(description = "2차 항목 식별자 (복수 선택 가능)", example = "1"))
            @RequestParam(name = "secondaryCategoryId", required = false)
            List<Integer> secondaryCategoryIds,

            @Parameter(schema = @Schema(description = "마지막 게시글 ID (첫 요청 시 생략)", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @RequestParam(name = "lastPostId", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다. ")
            String lastPostUlid,

            @Parameter(schema = @Schema(description = "마지막 게시글 발행 시점 (첫 요청 시 생략)"))
            @RequestParam(name = "lastPostPublishedAt", required = false)
            LocalDateTime lastPostPublishedAt,

            @Parameter(schema = @Schema(description = "마지막 게시글 중요도 (첫 요청 시 생략)", example = "1"))
            @RequestParam(name = "lastPostImportance", required = false)
            Integer lastPostImportance,

            @Parameter(schema = @Schema(description = "마지막 게시글 정확도 (첫 요청 시 생략)", example = "0.143253469630148"))
            @RequestParam(name = "lastPostSimilarity", required = false)
            Double lastPostSimilarity,

            @Parameter(schema = @Schema(description = "페이지 크기", example = "10", minimum = "1", maximum = "50"))
            @RequestParam
            @Min(value = 1, message = "페이지 크기가 허용된 값을 벗어났습니다. ")
            @Max(value = 50, message = "페이지 크기가 허용된 값을 벗어났습니다. ")
            Integer size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal
            DefaultUserDetails userDetails
    ) {
        UUID memberId = (userDetails != null) ? userDetails.getUuid() : null;
        return ResponseEntity.ok().body(DataResponse.ok(searchController.searchByKeyword(
                new SearchPostRecord(keyword, searchPostTarget, searchPostSortCondition, primaryCategoryId, secondaryCategoryIds,
                lastPostUlid, lastPostPublishedAt, lastPostImportance, lastPostSimilarity, size, memberId))));
    }

    @Operation(
            summary = "검색 기록 목록 조회 API",
            description = "키워드를 통한 게시글 목록 검색 시에 입력한 검색 기록 목록을 조회합니다. "
    )
    @GetMapping("/search-history")
    public ResponseEntity<DataResponse<List<String>>> getSearchHistory(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @Parameter(schema = @Schema(description = "검색 기록 개수", example = "10", minimum = "1", maximum = "20"))
            @RequestParam
            @Min(value = 1, message = "검색 기록 개수가 허용된 값을 벗어났습니다. ")
            @Max(value = 50, message = "검색 기록 개수가 허용된 값을 벗어났습니다. ")
            int size
    ) {
        UUID memberId = userDetails.getUuid();
        return ResponseEntity.ok().body(DataResponse.ok(searchController.getSearchHistory(memberId, size)));
    }

    @Operation(
            summary = "검색 기록 단건 삭제 API",
            description = "검색 기록 목록에서 검색 기록을 단건 삭제합니다."
    )
    @DeleteMapping("/search-history/{keyword}")
    public ResponseEntity<DataResponse<Void>> removeSearchKeyword(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @Parameter(schema = @Schema(description = "키워드", example = "벌레"))
            @PathVariable
            @NotBlank(message = "키워드가 비어 있습니다.")
            String keyword
    ) {
        UUID memberId = userDetails.getUuid();
        searchController.deleteSearchKeyword(keyword, memberId);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "검색 기록 전체 삭제 API",
            description = "검색 기록 목록에서 모든 검색 기록을 삭제합니다."
    )
    @DeleteMapping("/search-history")
    public ResponseEntity<DataResponse<Void>> removeAllSearchHistory(
            @AuthenticationPrincipal DefaultUserDetails userDetails
    ) {
        UUID memberId = userDetails.getUuid();
        searchController.deleteAllSearchHistory(memberId);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
