package kr.modusplant.legacy.domains.communication.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.framework.outbound.jackson.http.response.DataResponse;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCategoryInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.legacy.domains.communication.app.service.CommSecondaryCategoryApplicationService;
import kr.modusplant.legacy.domains.communication.domain.validation.CommunicationCategory;
import kr.modusplant.shared.validation.ZeroBasedOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "컨텐츠 2차 항목 API", description = "컨텐츠 2차 항목 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/communication/categories/secondary")
@RequiredArgsConstructor
@Validated
public class CommSecondaryCategoryController {
    private final CommSecondaryCategoryApplicationService commCategoryApplicationService;

    @Operation(
            summary = "전체 컨텐츠 2차 항목 조회 API",
            description = "전체 컨텐츠 2차 항목의 식별자를 비롯하여 이름, 컨텐츠와 버전 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<CommCategoryResponse>>> getAllCommCategories() {
        return ResponseEntity.ok().body(DataResponse.ok(commCategoryApplicationService.getAll()));
    }

    @Operation(
            summary = "UUID로 컨텐츠 2차 항목 조회 API",
            description = "UUID에 맞는 컨텐츠 2차 항목을 조회합니다."
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> getCommCategoryByUuid(
            @Parameter(schema = @Schema(
                    description = "2차 항목의 식별자",
                    example = "72197aeb-b1e7-4bd4-9116-bbcb9cd3f60d")
            )
            @PathVariable(required = false)
            @NotNull(message = "식별자가 비어 있습니다.")
            UUID uuid) {
        Optional<CommCategoryResponse> optionalCommCategoryResponse = commCategoryApplicationService.getByUuid(uuid);
        if (optionalCommCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalCommCategoryResponse.orElseThrow()));
    }

    @Operation(
            summary = "순서로 컨텐츠 2차 항목 조회 API",
            description = "순서에 맞는 컨텐츠 2차 항목을 조회합니다."
    )
    @GetMapping("/order/{order}")
    public ResponseEntity<DataResponse<?>> getCommCategoryByOrder(
            @Parameter(schema = @Schema(
                    description = "2차 항목이 렌더링되는 순서",
                    minimum = "0",
                    maximum = "100",
                    example = "3")
            )
            @PathVariable(required = false)
            @ZeroBasedOrder
            Integer order) {
        Optional<CommCategoryResponse> optionalCommCategoryResponse = commCategoryApplicationService.getByOrder(order);
        if (optionalCommCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalCommCategoryResponse.orElseThrow()));
    }

    @Operation(
            summary = "2차 항목으로 컨텐츠 2차 항목 조회 API",
            description = "2차 항목에 맞는 컨텐츠 2차 항목을 조회합니다."
    )
    @GetMapping("/category/{category}")
    public ResponseEntity<DataResponse<?>> getCommCategoryByName(
            @Parameter(schema = @Schema(
                    description = "2차 항목",
                    maxLength = 40,
                    example = "삽목 + 포기 나누기")
            )
            @PathVariable(required = false)
            @CommunicationCategory
            String category) {
        Optional<CommCategoryResponse> optionalCommCategoryResponse = commCategoryApplicationService.getByCategory(category);
        if (optionalCommCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalCommCategoryResponse.orElseThrow()));
    }

    @Operation(
            summary = "컨텐츠 2차 항목 삽입 API",
            description = "순서, 2차 항목 정보로 컨텐츠 2차 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<CommCategoryResponse>> insertCommCategory(@RequestBody @Valid CommCategoryInsertRequest commCategoryInsertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(commCategoryApplicationService.insert(commCategoryInsertRequest)));
    }

    @Operation(
            summary = "컨텐츠 2차 항목 제거 API",
            description = "UUID로 컨텐츠 2차 항목을 제거합니다."
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> removeCommCategoryByUuid(
            @Parameter(schema = @Schema(
                    description = "2차 항목의 식별자",
                    example = "72197aeb-b1e7-4bd4-9116-bbcb9cd3f60d")
            )
            @PathVariable(required = false)
            @NotNull(message = "식별자가 비어 있습니다.")
            UUID uuid) {
        commCategoryApplicationService.removeByUuid(uuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
