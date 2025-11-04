package kr.modusplant.domains.member.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.usecase.request.*;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "회원 닉네임 갱신 API", description = "회원 닉네임을 갱신합니다.")
    @PostMapping("/nickname")
    public ResponseEntity<DataResponse<MemberResponse>> updateMemberNickname(
            @RequestBody @Valid MemberNicknameUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataResponse.ok(memberController.updateNickname(request)));
    }

    @Operation(summary = "게시글 좋아요 API", description = "게시글에 좋아요를 누릅니다.")
    @PutMapping("/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationPost(
            @RequestBody @Valid MemberPostLikeRequest request) {
        memberController.likePost(request);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "게시글 좋아요 취소 API", description = "게시글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/like/communication/post/{postUlid}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationPost(
            @RequestBody @Valid MemberPostUnlikeRequest request) {
        memberController.unlikePost(request);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "댓글 좋아요 API", description = "댓글에 좋아요를 누릅니다.")
    @PutMapping("/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> likeCommunicationComment(
            @RequestBody @Valid MemberCommentLikeRequest request) {
        memberController.likeComment(request);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "댓글 좋아요 취소 API", description = "댓글에 대한 좋아요를 취소합니다.")
    @DeleteMapping("/like/communication/post/{postUlid}/path/{path}")
    public ResponseEntity<DataResponse<Void>> unlikeCommunicationComment(
            @RequestBody @Valid MemberCommentUnlikeRequest request) {
        memberController.unlikeComment(request);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
