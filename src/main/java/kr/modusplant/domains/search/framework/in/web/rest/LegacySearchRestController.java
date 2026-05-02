package kr.modusplant.domains.search.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.search.adapter.controller.SearchController;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "검색 API", description = "검색 기능을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class LegacySearchRestController {
    private final SearchController searchController;

    @Operation(
            summary = "검색 기록 목록 조회 API",
            description = "키워드를 통한 게시글 목록 검색 시에 입력한 검색 기록 목록을 조회합니다. "
    )
    @GetMapping("/search-history")
    public ResponseEntity<DataResponse<List<String>>> getSearchHistory(
            @Parameter(schema = @Schema(description = "검색 기록 개수", example = "10", minimum = "1", maximum = "20"))
            @RequestParam
            @Min(value = 1, message = "검색 기록 개수가 허용된 값을 벗어났습니다. ")
            @Max(value = 50, message = "검색 기록 개수가 허용된 값을 벗어났습니다. ")
            int size,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        return ResponseEntity.ok().body(DataResponse.ok(searchController.getSearchHistory(memberId, size)));
    }

    @Operation(
            summary = "검색 기록 단건 삭제 API",
            description = "검색 기록 목록에서 검색 기록을 단건 삭제합니다."
    )
    @DeleteMapping("/search-history/{keyword}")
    public ResponseEntity<DataResponse<Void>> removeSearchKeyword(
            @Parameter(schema = @Schema(description = "키워드", example = "벌레"))
            @PathVariable
            @NotBlank(message = "키워드가 비어 있습니다.")
            String keyword,

            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        searchController.deleteSearchKeyword(keyword, memberId);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "검색 기록 전체 삭제 API",
            description = "검색 기록 목록에서 모든 검색 기록을 삭제합니다."
    )
    @DeleteMapping("/search-history")
    public ResponseEntity<DataResponse<Void>> removeAllSearchHistory(
            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId) {
        searchController.deleteAllSearchHistory(memberId);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
