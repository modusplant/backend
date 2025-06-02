package kr.modusplant.domains.communication.conversation.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCategoryInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvCategoryApplicationService;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "ConvCategory API", description = "대화 항목 API")
@RestController
@Primary
@RequestMapping("/api/crud/conversation/categories")
@RequiredArgsConstructor
public class ConvCategoryController {
    private final ConvCategoryApplicationService convCategoryApplicationService;

    @Operation(summary = "전체 대화 항목 조회 API", description = "전체 대화 항목의 식별자를 비롯하여 이름, 컨텐츠와 버전 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<DataResponse<List<ConvCategoryResponse>>> getAllConvCategories() {
        return ResponseEntity.ok().body(DataResponse.ok(convCategoryApplicationService.getAll()));
    }

    @Operation(summary = "UUID로 대화 항목 조회 API", description = "UUID에 맞는 대화 항목을 조회합니다.")
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> getConvCategoryByUuid(@PathVariable UUID uuid) {
        Optional<ConvCategoryResponse> optionalConvCategoryResponse = convCategoryApplicationService.getByUuid(uuid);
        if (optionalConvCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalConvCategoryResponse.orElseThrow()));
    }

    @Operation(summary = "순서로 대화 항목 조회 API", description = "순서에 맞는 대화 항목을 조회합니다.")
    @GetMapping("/order/{order}")
    public ResponseEntity<DataResponse<?>> getConvCategoryByOrder(@PathVariable Integer order) {
        Optional<ConvCategoryResponse> optionalConvCategoryResponse = convCategoryApplicationService.getByOrder(order);
        if (optionalConvCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalConvCategoryResponse.orElseThrow()));
    }

    @Operation(summary = "항목으로 대화 항목 조회 API", description = "항목에 맞는 대화 항목을 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<DataResponse<?>> getConvCategoryByName(@PathVariable String category) {
        Optional<ConvCategoryResponse> optionalConvCategoryResponse = convCategoryApplicationService.getByCategory(category);
        if (optionalConvCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalConvCategoryResponse.orElseThrow()));
    }

    @Operation(summary = "대화 항목 삽입 API", description = "순서, 항목 정보로 대화 항목을 삽입합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<ConvCategoryResponse>> insertConvCategory(@RequestBody ConvCategoryInsertRequest convCategoryInsertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(convCategoryApplicationService.insert(convCategoryInsertRequest)));
    }

    @Operation(summary = "대화 항목 제거 API", description = "UUID로 대화 항목을 제거합니다.")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> removeConvCategoryByUuid(@PathVariable UUID uuid) {
        convCategoryApplicationService.removeByUuid(uuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
