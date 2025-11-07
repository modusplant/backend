package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.*;

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
            @RequestBody @Valid MemberRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.register(request)));
    }

    @Operation(summary = "회원 프로필 갱신 API", description = "회원 프로필을 갱신합니다.")
    @PostMapping("/{id}/profile")
    public ResponseEntity<DataResponse<MemberProfileResponse>> updateMemberProfile(
            @Parameter(description = "기존에 저장된 회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "갱신할 회원의 프로필 이미지", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(name = "image")
            @NotNull(message = "회원 프로필 이미지가 비어 있습니다. ")
            MultipartFile image,

            @Parameter(description = "갱신할 회원의 프로필 소개", example = "프로필 소개")
            @RequestPart(name = "introduction")
            @NotNull(message = "회원 프로필 소개가 비어 있습니다. ")
            String introduction,

            @Parameter(description = "갱신할 회원의 닉네임", example = "NewPlayer")
            @RequestPart(name = "nickname")
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME, message = "회원 닉네임 서식이 올바르지 않습니다. ")
            String nickname) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.updateProfile(new MemberProfileUpdateRecord(id, introduction, image, nickname))));
    }

    @Operation(summary = "게시글 좋아요 API", description = "게시글에 좋아요를 누릅니다.")
    @PutMapping("/{id}/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationPost(
            @Parameter(description = "회원 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "좋아요를 누를 게시글의 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid) {
        memberController.likePost(new MemberPostLikeRecord(id, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "게시글 좋아요 취소 API", description = "게시글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/{id}/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationPost(
            @Parameter(description = "회원 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "좋아요를 취소할 게시글의 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid) {
        memberController.unlikePost(new MemberPostUnlikeRecord(id, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "댓글 좋아요 API", description = "댓글에 좋아요를 누릅니다.")
    @PutMapping("/{id}/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationComment(
            @Parameter(description = "회원 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "좋아요를 누를 댓글의 게시글 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(description = "좋아요를 누를 댓글의 경로", example = "1.0.4", schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH))
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path) {
        memberController.likeComment(new MemberCommentLikeRecord(id, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "댓글 좋아요 취소 API", description = "댓글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/{id}/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationComment(
            @Parameter(description = "회원 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "좋아요를 취소할 댓글의 게시글 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(description = "좋아요를 취소할 댓글의 경로", example = "1.0.4", schema = @Schema(type = "string", pattern = REGEX_MATERIALIZED_PATH))
            @PathVariable(required = false)
            @NotBlank(message = "댓글 경로가 비어 있습니다.")
            String path) {
        memberController.unlikeComment(new MemberCommentUnlikeRecord(id, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
