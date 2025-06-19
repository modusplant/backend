package kr.modusplant.domains.term.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.domains.term.app.http.request.TermUpdateRequest;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.global.domain.validation.SemanticVersioning;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "약관 API", description = "약관을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
@Validated
public class TermController {
    private final TermApplicationService termApplicationService;

    @Operation(summary = "전체 약관 조회 API", description = "전체 약관의 식별자를 비롯하여 이름, 컨텐츠와 버전 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<DataResponse<List<TermResponse>>> getAllTerms() {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.getAll()));
    }

    @Operation(summary = "버전으로 약관 조회 API", description = "버전에 맞는 약관을 조회합니다.")
    @GetMapping("/version/{version}")
    public ResponseEntity<DataResponse<List<TermResponse>>> getTermsByVersion(@PathVariable(required = false)
                                                                              @SemanticVersioning
                                                                              String version) {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.getByVersion(version)));
    }

    @Operation(summary = "식별자로 약관 조회 API", description = "식별자에 맞는 약관을 조회합니다.")
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> getTermByUuid(@PathVariable(required = false) UUID uuid) {
        Optional<TermResponse> optionalTermResponse = termApplicationService.getByUuid(uuid);
        if (optionalTermResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTermResponse.orElseThrow()));
    }

    @Operation(summary = "이름으로 약관 조회 API", description = "이름에 맞는 약관을 조회합니다.")
    @GetMapping("/name/{name}")
    public ResponseEntity<DataResponse<?>> getTermByName(@PathVariable(required = false)
                                                         @NotBlank(message = "이름이 비어 있습니다.")
                                                         String name) {
        Optional<TermResponse> optionalTermResponse = termApplicationService.getByName(name);
        if (optionalTermResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTermResponse.orElseThrow()));
    }

    @Operation(summary = "약관 삽입 API", description = "이름, 컨텐츠, 버전 정보로 약관을 삽입합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<TermResponse>> insertTerm(@RequestBody @Valid TermInsertRequest termInsertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.insert(termInsertRequest)));
    }

    @Operation(summary = "약관 갱신 API", description = "식별자, 컨텐츠, 버전 정보로 약관을 갱신합니다.")
    @PutMapping
    public ResponseEntity<DataResponse<TermResponse>> updateTerm(@RequestBody @Valid TermUpdateRequest termUpdateRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.update(termUpdateRequest)));
    }

    @Operation(summary = "약관 제거 API", description = "식별자로 약관을 제거합니다.")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> removeTermByUuid(@PathVariable(required = false) UUID uuid) {
        termApplicationService.removeByUuid(uuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
