package kr.modusplant.domains.term.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TermUpdateRequest(
        @Schema(description = "약관아이디", example = "3566cbd8-069a-4c9d-980f-74a2599a4413")
        @NotNull(message = "약관아이디가 비어 있습니다. ")
        UUID termId,
        @NotBlank(message = "약관내용이 비어 있습니다. ")
        String termContent
) {
}
