package kr.modusplant.domains.communication.tip.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationCategory;
import kr.modusplant.domains.communication.tip.app.http.request.TipCategoryInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.app.service.TipCategoryApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.global.domain.validation.ZeroBasedOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "팁 항목 API", description = "팁 항목 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/tip/categories")
@RequiredArgsConstructor
@Validated
public class TipCategoryController {
    private final TipCategoryApplicationService tipCategoryApplicationService;

    @Operation(
            summary = "전체 팁 항목 조회 API",
            description = "전체 팁 항목의 식별자를 비롯하여 이름, 컨텐츠와 버전 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<TipCategoryResponse>>> getAllTipCategories() {
        return ResponseEntity.ok().body(DataResponse.ok(tipCategoryApplicationService.getAll()));
    }

    @Operation(
            summary = "UUID로 팁 항목 조회 API",
            description = "UUID에 맞는 팁 항목을 조회합니다."
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> getTipCategoryByUuid(@PathVariable
                                                                @NotNull(message = "식별자가 비어 있습니다.")
                                                                UUID uuid) {
        Optional<TipCategoryResponse> optionalTipCategoryResponse = tipCategoryApplicationService.getByUuid(uuid);
        if (optionalTipCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTipCategoryResponse.orElseThrow()));
    }

    @Operation(
            summary = "순서로 팁 항목 조회 API",
            description = "순서에 맞는 팁 항목을 조회합니다."
    )
    @GetMapping("/order/{order}")
    public ResponseEntity<DataResponse<?>> getTipCategoryByOrder(@PathVariable @ZeroBasedOrder Integer order) {
        Optional<TipCategoryResponse> optionalTipCategoryResponse = tipCategoryApplicationService.getByOrder(order);
        if (optionalTipCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTipCategoryResponse.orElseThrow()));
    }

    @Operation(
            summary = "항목으로 팁 항목 조회 API",
            description = "항목에 맞는 팁 항목을 조회합니다."
    )
    @GetMapping("/category/{category}")
    public ResponseEntity<DataResponse<?>> getTipCategoryByName(@PathVariable @CommunicationCategory String category) {
        Optional<TipCategoryResponse> optionalTipCategoryResponse = tipCategoryApplicationService.getByCategory(category);
        if (optionalTipCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTipCategoryResponse.orElseThrow()));
    }

    @Operation(
            summary = "팁 항목 삽입 API",
            description = "순서, 항목 정보로 팁 항목을 삽입합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<TipCategoryResponse>> insertTipCategory(@RequestBody @Valid TipCategoryInsertRequest tipCategoryInsertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(tipCategoryApplicationService.insert(tipCategoryInsertRequest)));
    }

    @Operation(
            summary = "팁 항목 제거 API",
            description = "UUID로 팁 항목을 제거합니다."
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> removeTipCategoryByUuid(@PathVariable
                                                                   @NotNull(message = "식별자가 비어 있습니다.")
                                                                   UUID uuid) {
        tipCategoryApplicationService.removeByUuid(uuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
