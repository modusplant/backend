package kr.modusplant.domains.communication.qna.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.qna.app.http.request.QnaCategoryInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCategoryResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaCategoryApplicationService;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "QnaCategory API", description = "Q&A 항목 API")
@RestController
@Primary
@RequestMapping("/api/v1/qna/categories")
@RequiredArgsConstructor
public class QnaCategoryController {
    private final QnaCategoryApplicationService qnaCategoryApplicationService;

    @Operation(summary = "전체 Q&A 항목 조회 API", description = "전체 Q&A 항목의 식별자를 비롯하여 이름, 컨텐츠와 버전 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<DataResponse<List<QnaCategoryResponse>>> getAllQnaCategories() {
        return ResponseEntity.ok().body(DataResponse.ok(qnaCategoryApplicationService.getAll()));
    }

    @Operation(summary = "UUID로 Q&A 항목 조회 API", description = "UUID에 맞는 Q&A 항목을 조회합니다.")
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> getQnaCategoryByUuid(@PathVariable UUID uuid) {
        Optional<QnaCategoryResponse> optionalQnaCategoryResponse = qnaCategoryApplicationService.getByUuid(uuid);
        if (optionalQnaCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalQnaCategoryResponse.orElseThrow()));
    }

    @Operation(summary = "순서로 Q&A 항목 조회 API", description = "순서에 맞는 Q&A 항목을 조회합니다.")
    @GetMapping("/order/{order}")
    public ResponseEntity<DataResponse<?>> getQnaCategoryByOrder(@PathVariable Integer order) {
        Optional<QnaCategoryResponse> optionalQnaCategoryResponse = qnaCategoryApplicationService.getByOrder(order);
        if (optionalQnaCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalQnaCategoryResponse.orElseThrow()));
    }

    @Operation(summary = "항목으로 Q&A 항목 조회 API", description = "항목에 맞는 Q&A 항목을 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<DataResponse<?>> getQnaCategoryByName(@PathVariable String category) {
        Optional<QnaCategoryResponse> optionalQnaCategoryResponse = qnaCategoryApplicationService.getByCategory(category);
        if (optionalQnaCategoryResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalQnaCategoryResponse.orElseThrow()));
    }

    @Operation(summary = "Q&A 항목 삽입 API", description = "순서, 항목 정보로 Q&A 항목을 삽입합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<QnaCategoryResponse>> insertQnaCategory(@RequestBody QnaCategoryInsertRequest qnaCategoryInsertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(qnaCategoryApplicationService.insert(qnaCategoryInsertRequest)));
    }

    @Operation(summary = "Q&A 항목 제거 API", description = "UUID로 Q&A 항목을 제거합니다.")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> removeQnaCategoryByUuid(@PathVariable UUID uuid) {
        qnaCategoryApplicationService.removeByUuid(uuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
