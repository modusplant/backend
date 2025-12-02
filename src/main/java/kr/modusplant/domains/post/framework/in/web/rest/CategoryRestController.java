package kr.modusplant.domains.post.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.post.adapter.controller.CategoryController;
import kr.modusplant.domains.post.usecase.response.PrimaryCategoryResponse;
import kr.modusplant.domains.post.usecase.response.SecondaryCategoryResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "게시글 항목 API", description = "게시글 항목을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/communication")
@RequiredArgsConstructor
@Validated
public class CategoryRestController {

    private final CategoryController categoryController;

    @Operation(
            summary = "게시글 1차 항목 목록 조회 API",
            description = "게시글 1차 항목 목록을 조회합니다."
    )
    @GetMapping("/primary-categories")
    public ResponseEntity<DataResponse<List<PrimaryCategoryResponse>>> getPrimaryCategories() {
        return ResponseEntity.ok().body(DataResponse.ok(categoryController.getPrimaryCategories()));
    }

    @Operation(
            summary = "게시글 2차 항목 목록 조회 API",
            description = "게시글 1차 항목의 2차 항목 목록을 조회합니다."
    )
    @GetMapping("/primary-categories/{primaryCategoryId}/secondary-categories")
    public ResponseEntity<DataResponse<List<SecondaryCategoryResponse>>> getSecondaryCategoriesByPrimaryCategory(
            @Parameter(schema = @Schema(description = "1차 항목의 식별자", example = "038ae842-3c93-484f-b526-7c4645a195a7"))
            @PathVariable(name = "primaryCategoryId")
            @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
            UUID primaryCategoryUuid
    ) {
        return ResponseEntity.ok().body(DataResponse.ok(categoryController.getSecondaryCategoriesByPrimaryCategory(primaryCategoryUuid)));
    }
}
