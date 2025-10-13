package kr.modusplant.domains.term.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.member.usecase.request.TermCreateRequest;
import kr.modusplant.domains.term.adaptor.controller.TermController;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "약관 API", description = "약관의 생성, 수정 기능을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
@Validated
public class TermRestController {
    private final TermController termController;

    @Operation(summary = "약관 등록 API", description = "약관을 등록합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<TermResponse>> registerTerm(
            @RequestBody @Valid TermCreateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(termController.register(request)));
    }

    @Operation(summary = "약관 삭제 API", description = "약관을 삭제합니다.")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<Void>> deleteTerm(
        @PathVariable @NotNull UUID uuid) {
        termController.delete(TermId.fromUuid(uuid));
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.ok());
    }
}
