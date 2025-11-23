package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.domain.exception.IncorrectMemberIdException;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenExpiredException;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.*;

@Tag(name = "회원 API", description = "회원의 생성과 갱신(상태 제외), 회원이 할 수 있는 단일한 기능을 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Validated
public class MemberRestController {
    private final MemberController memberController;
    private final JwtTokenProvider jwtTokenProvider;

    @Hidden
    @Operation(summary = "회원 등록 API", description = "닉네임을 통해 회원을 등록합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<MemberResponse>> registerMember(
            @RequestBody @Valid MemberRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.register(request)));
    }

    @Operation(summary = "회원 닉네임 중복 확인 API", description = "이미 등록된 닉네임이 있는지 조회합니다.")
    @GetMapping(value = "/check/nickname/{nickname}")
    public ResponseEntity<DataResponse<Map<String, Boolean>>> checkExistedMemberNickname(
            @Parameter(description = "중복을 확인하려는 회원의 닉네임", example = "IsThisNickname", schema = @Schema(type = "string", pattern = REGEX_NICKNAME))
            @PathVariable(required = false)
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME,
                    message = "회원 닉네임 서식이 올바르지 않습니다. ")
            String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.ok(Map.of(
                "isNicknameExisted", memberController.checkExistedNickname(new MemberNicknameCheckRecord(nickname))))
        );
    }

    @Operation(
            summary = "회원 프로필 조회 API",
            description = "기존 회원 프로필을 조회합니다. ",
            security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping(value = "/{id}/profile")
    public ResponseEntity<DataResponse<MemberProfileResponse>> getMemberProfile(
            @Parameter(description = "기존에 저장된 회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) throws IOException {
        validateTokenAndAccessToId(id, auth);
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.getProfile(new MemberProfileGetRecord(id))));
    }

    @Operation(
            summary = "회원 프로필 덮어쓰기 API",
            description = "기존 회원 프로필을 덮어씁니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @PutMapping(value = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<MemberProfileResponse>> overrideMemberProfile(
            @Parameter(description = "기존에 저장된 회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "갱신할 회원의 프로필 이미지", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(name = "image", required = false)
            MultipartFile image,

            @Parameter(description = "갱신할 회원의 프로필 소개", example = "프로필 소개")
            @RequestPart(name = "introduction", required = false)
            String introduction,

            @Parameter(description = "갱신할 회원의 닉네임", example = "NewPlayer", schema = @Schema(type = "string", pattern = REGEX_NICKNAME))
            @RequestPart(name = "nickname")
            @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
            @Pattern(regexp = REGEX_NICKNAME, message = "회원 닉네임 서식이 올바르지 않습니다. ")
            String nickname,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) throws IOException {
        validateTokenAndAccessToId(id, auth);
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.overrideProfile(new MemberProfileOverrideRecord(id, introduction, image, nickname))));
    }

    @Operation(
            summary = "게시글 좋아요 API",
            description = "게시글에 좋아요를 누릅니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @PutMapping("/{id}/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationPost(
            @Parameter(description = "회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "좋아요를 누를 게시글의 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        validateTokenAndAccessToId(id, auth);
        memberController.likePost(new MemberPostLikeRecord(id, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "게시글 좋아요 취소 API",
            description = "게시글에 대한 좋아요를 취소합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @DeleteMapping("/{id}/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationPost(
            @Parameter(description = "회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "좋아요를 취소할 게시글의 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        validateTokenAndAccessToId(id, auth);
        memberController.unlikePost(new MemberPostUnlikeRecord(id, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "게시글 북마크 API",
            description = "게시글에 북마크를 누릅니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @PutMapping("/{id}/bookmark/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> bookmarkCommunicationPost(
            @Parameter(description = "회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "북마크를 누를 게시글의 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        validateTokenAndAccessToId(id, auth);
        memberController.bookmarkPost(new MemberPostBookmarkRecord(id, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "게시글 북마크 취소 API",
            description = "게시글에 대한 북마크를 취소합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @DeleteMapping("/{id}/bookmark/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> cancelCommunicationPostBookmark(
            @Parameter(description = "회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
            @PathVariable(required = false)
            @NotNull(message = "회원 아이디가 비어 있습니다. ")
            UUID id,

            @Parameter(description = "북마크를 취소할 게시글의 식별자", schema = @Schema(type = "string", format = "ulid", pattern = REGEX_ULID))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String postUlid,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        validateTokenAndAccessToId(id, auth);
        memberController.cancelPostBookmark(new MemberPostBookmarkCancelRecord(id, postUlid));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "댓글 좋아요 API",
            description = "댓글에 좋아요를 누릅니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @PutMapping("/{id}/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationComment(
            @Parameter(description = "회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
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
            String path,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        validateTokenAndAccessToId(id, auth);
        memberController.likeComment(new MemberCommentLikeRecord(id, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "댓글 좋아요 취소 API",
            description = "댓글에 대한 좋아요를 취소합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @DeleteMapping("/{id}/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationComment(
            @Parameter(description = "회원의 아이디", schema = @Schema(type = "string", format = "uuid", pattern = REGEX_UUID))
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
            String path,

            @Parameter(hidden = true)
            @RequestHeader(name = "Authorization")
            @NotNull(message = "접근 토큰이 비어 있습니다. ")
            String auth) {
        validateTokenAndAccessToId(id, auth);
        memberController.unlikeComment(new MemberCommentUnlikeRecord(id, postUlid, path));
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    private void validateTokenAndAccessToId(UUID id, String auth) {
        String accessToken;
        if (auth.startsWith("Bearer ")) {
            accessToken = auth.substring(7);
        } else {
            throw new InvalidTokenException();
        }
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new TokenExpiredException();
        }
        if (!jwtTokenProvider.getMemberUuidFromToken(accessToken).equals(id)) {
            throw new IncorrectMemberIdException();
        }
    }
}