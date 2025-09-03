package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.adapter.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "회원 API", description = "회원의 생성과 갱신(상태 제외), 회원이 할 수 있는 단일한 기능을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Validated
public class MemberRestController {
    private final MemberController memberController;

    @Operation(summary = "회원 등록 API", description = "닉네임을 통해 회원을 등록합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<MemberResponse>> registerMember(
            @Parameter(schema = @Schema(
                    description = "회원을 등록하기 위한 요청")
            )
            MemberRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.ok(memberController.register(request)));
    }

    @Operation(summary = "회원 닉네임 갱신 API", description = "회원의 닉네임을 갱신합니다.")
    @PostMapping("/nickname")
    public ResponseEntity<DataResponse<MemberResponse>> updateMemberNickname(
            @Parameter(schema = @Schema(
                    description = "회원의 닉네임을 갱신하기 위한 요청")
            )
            MemberNicknameUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.ok(memberController.updateNickname(request)));
    }

    @Operation(summary = "소통 컨텐츠 게시글 좋아요 API", description = "소통 컨텐츠 게시글에 좋아요를 누릅니다.")
    @PutMapping("/like/communication/posts/{postUlid}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationPost(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID memberId,

            @Parameter(schema = @Schema(
                    description = "좋아요를 누를 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid) {
        memberController.likePost(memberId, postUlid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "소통 컨텐츠 게시글 좋아요 취소 API", description = "소통 컨텐츠 게시글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/like/communication/posts/{postUlid}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationPost(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID memberId,

            @Parameter(schema = @Schema(
                    description = "좋아요를 취소할 게시글의 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid) {
        memberController.unlikePost(memberId, postUlid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
