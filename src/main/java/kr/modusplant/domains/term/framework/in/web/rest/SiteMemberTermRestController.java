package kr.modusplant.domains.term.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.term.adaptor.controller.SiteMemberTermController;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import kr.modusplant.domains.term.usecase.request.SiteMemberTermCreateRequest;
import kr.modusplant.domains.term.usecase.request.SiteMemberTermUpdateRequest;
import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "사이트 회원 약관 API", description = "사이트 회원 약관의 생성, 수정, 조회 기능을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1/member/terms")
@RequiredArgsConstructor
@Validated
public class SiteMemberTermRestController {
    private final SiteMemberTermController siteMemberTermController;

    @Operation(summary = "사이트 회원 약관 등록 API", description = "사이트 회원 약관을 등록합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<SiteMemberTermResponse>> registerSiteMemberTerm(
            @RequestBody @Valid SiteMemberTermCreateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(siteMemberTermController.register(request)));
    }

    @Operation(summary = "사이트 회원 약관 수정 API", description = "사이트 회원 약관을 수정합니다.")
    @PutMapping
    public ResponseEntity<DataResponse<SiteMemberTermResponse>> updateSiteMemberTerm(
            @RequestBody @Valid SiteMemberTermUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(siteMemberTermController.update(request)));
    }

    @Operation(summary = "사이트 회원 약관 삭제 API", description = "사이트 회원 약관을 삭제합니다.")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<Void>> deleteSiteMemberTerm(
        @PathVariable @NotNull UUID uuid) {
        siteMemberTermController.delete(SiteMemberTermId.fromUuid(uuid));
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.ok());
    }

    @Operation(summary = "사이트 회원 약관 조회 API", description = "사이트 회원 약관을 조회합니다.")
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<SiteMemberTermResponse>> getSiteMemberTerm(@PathVariable @NotNull UUID uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(siteMemberTermController.getSiteMemberTerm(SiteMemberTermId.fromUuid(uuid))));
    }
    @Operation(summary = "사이트 회원 약관 목록조회 API", description = "사이트 회원 약관 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<DataResponse<List<SiteMemberTermResponse>>> getSiteMemberTermList() {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(siteMemberTermController.getSiteMemberTermList()));
    }
}
