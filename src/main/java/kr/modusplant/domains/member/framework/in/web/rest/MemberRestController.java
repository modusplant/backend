package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.REGEX_NICKNAME;

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
                    description = "회원 닉네임",
                    example = "ModusPlantPlayer")
            )
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME,
                    message = "회원 닉네임 서식이 올바르지 않습니다. ")
            String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.register(
                        MemberNickname.create(nickname))));
    }

    @Operation(summary = "회원 닉네임 갱신 API", description = "회원 닉네임을 갱신합니다.")
    @PostMapping("/nickname")
    public ResponseEntity<DataResponse<MemberResponse>> updateMemberNickname(
            @Parameter(schema = @Schema(
                    description = "기존에 저장된 회원의 아이디",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(schema = @Schema(
                    description = "갱신할 회원의 닉네임",
                    example = "NewPlayer")
            )
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME,
                    message = "회원 닉네임 서식이 올바르지 않습니다. ")
            String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.updateNickname(
                        MemberId.fromUuid(id), MemberNickname.create(nickname))));
    }

    @Operation(summary = "소통 게시글 좋아요 API", description = "소통 게시글에 좋아요를 누릅니다.")
    @PutMapping("/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationPost(
            @Parameter(schema = @Schema(
                    description = "회원 아이디",
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
        memberController.likePost(MemberId.fromUuid(memberId), TargetPostId.create(postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "소통 게시글 좋아요 취소 API", description = "소통 게시글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationPost(
            @Parameter(schema = @Schema(
                    description = "회원 아이디",
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
        memberController.unlikePost(MemberId.fromUuid(memberId), TargetPostId.create(postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "소통 댓글 좋아요 API", description = "소통 댓글에 좋아요를 누릅니다.")
    @PutMapping("/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationComment(
            @Parameter(schema = @Schema(
                    description = "회원 아이디",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID memberId,

            @Parameter(schema = @Schema(
                    description = "좋아요를 누를 댓글의 게시글 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(schema = @Schema(
                    description = "좋아요를 누를 댓글의 경로",
                    example = "1.0.4")
            )
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path) {
        memberController.likeComment(MemberId.fromUuid(memberId), TargetCommentId.create(TargetPostId.create(postUlid), TargetCommentPath.create(path)));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "소통 댓글 좋아요 취소 API", description = "소통 댓글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationComment(
            @Parameter(schema = @Schema(
                    description = "회원 아이디",
                    example = "038ae842-3c93-484f-b526-7c4645a195a7")
            )
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID memberId,

            @Parameter(schema = @Schema(
                    description = "좋아요를 취소할 댓글의 게시글 식별자",
                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(schema = @Schema(
                    description = "좋아요를 취소할 댓글의 경로",
                    example = "1.0.4")
            )
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path) {
        memberController.unlikeComment(MemberId.fromUuid(memberId), TargetCommentId.create(TargetPostId.create(postUlid), TargetCommentPath.create(path)));
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
