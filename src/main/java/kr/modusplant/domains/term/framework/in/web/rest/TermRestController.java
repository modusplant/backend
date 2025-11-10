package kr.modusplant.domains.term.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.term.usecase.request.TermCreateRequest;
import kr.modusplant.domains.term.adaptor.controller.TermController;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.usecase.request.TermUpdateRequest;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "약관 API", description = "약관의 생성, 수정, 조회 기능을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
@Validated
public class TermRestController {
    private final TermController termController;

    @Operation(summary = "약관 목록조회 API", description = "세가지 약관 목록을 조회합니다.(이용약관, 개인정보처리방침, 광고성 정보)")
    @GetMapping
    public ResponseEntity<DataResponse<List<TermResponse>>> getTermList() {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(termController.getTermList()));
    }
//
//    @Operation(summary = "약관 등록 API", description = "약관을 등록합니다.")
//    @PostMapping
//    public ResponseEntity<DataResponse<TermResponse>> registerTerm(
//            @RequestBody @Valid TermCreateRequest request) {
//        return ResponseEntity.status(HttpStatus.OK).body(
//                DataResponse.ok(termController.register(request)));
//    }
//
//    @Operation(summary = "약관 수정 API", description = "약관을 수정합니다.")
//    @PutMapping
//    public ResponseEntity<DataResponse<TermResponse>> updateTerm(
//            @RequestBody @Valid TermUpdateRequest request) {
//        return ResponseEntity.status(HttpStatus.OK).body(
//                DataResponse.ok(termController.update(request)));
//    }
//
//    @Operation(summary = "약관 삭제 API", description = "약관을 삭제합니다.")
//    @DeleteMapping("/{uuid}")
//    public ResponseEntity<DataResponse<Void>> deleteTerm(
//        @PathVariable @NotNull UUID uuid) {
//        termController.delete(TermId.fromUuid(uuid));
//        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.ok());
//    }
//
//    @Operation(summary = "약관 조회 API", description = "약관을 조회합니다.")
//    @GetMapping("/{uuid}")
//    public ResponseEntity<DataResponse<TermResponse>> getTerm(@PathVariable @NotNull UUID uuid) {
//        return ResponseEntity.status(HttpStatus.OK).body(
//                DataResponse.ok(termController.getTerm(TermId.fromUuid(uuid))));
//    }
}
