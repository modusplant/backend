package kr.modusplant.domains.record.framework.inbound.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.modusplant.domains.record.adapter.controller.RecordController;
import kr.modusplant.domains.record.usecase.request.RecordCreateRequest;
import kr.modusplant.domains.record.usecase.request.RecordUploadUrlRequest;
import kr.modusplant.domains.record.usecase.response.RecordCreateResponse;
import kr.modusplant.domains.record.usecase.response.RecordUploadUrlResponse;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "모두의기록 API", description = "식물 영상 기록을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class RecordRestController {

    private final RecordController recordController;

    @Operation(
            summary = "영상 업로드 URL 발급 API",
            description = "영상 업로드를 위한 Presigned PUT URL과 등록 API용 uploadToken을 발급합니다."
    )
    @PostMapping("/upload-urls")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DataResponse<RecordUploadUrlResponse>> createUploadUrl(
            @AuthenticationPrincipal DefaultUserDetails userDetails,
            @Valid @RequestBody RecordUploadUrlRequest request
    ) {
        return ResponseEntity.ok().body(
                DataResponse.ok(recordController.createUploadUrl(userDetails.getUuid(), request))
        );
    }

    @Operation(
            summary = "기록 등록 API",
            description = "업로드된 영상을 검증하고 썸네일을 생성해 기록을 최종 등록합니다."
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DataResponse<RecordCreateResponse>> createRecord(
            @AuthenticationPrincipal DefaultUserDetails userDetails,
            @Valid @RequestBody RecordCreateRequest request
    ) {
        return ResponseEntity.ok().body(
                DataResponse.ok(recordController.createRecord(userDetails.getUuid(), request))
        );
    }
}
