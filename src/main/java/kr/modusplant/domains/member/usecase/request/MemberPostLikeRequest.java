package kr.modusplant.domains.member.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public record MemberPostLikeRequest(
        @Schema(description = "회원 아이디",
                type = "UUID")
        @NotNull(message = "회원 아이디가 비어 있습니다. ")
        UUID memberId,

        @Schema(description = "좋아요를 누를 게시글의 식별자",
                type = "ULID")
        @PathVariable(required = false)
        @NotBlank(message = "게시글 식별자가 비어 있습니다.")
        String postUlid) {
}
